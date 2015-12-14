# Free-form

[![Code at GitHub](https://img.shields.io/badge/code-github-green.svg)](https://github.com/carouselapps/free-form)
[![Clojars](https://img.shields.io/clojars/v/com.carouselapps/free-form.svg)](https://clojars.org/com.carouselapps/free-form)
[![Join the chat at https://gitter.im/carouselapps/free-form](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/carouselapps/free-form?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

A ClojureScript library to help building web forms with [Reagent](https://reagent-project.github.io/) and optionally
[Re-frame](https://github.com/Day8/re-frame) (others are welcome too). The guiding principles behind [Free-form](https://carouselapps.com/free-form)
is that you are in control of both you data workflow as well as your markup. You can see the library in action in the
[Free-form Examples app](http://free-form-examples.carouselapps.com).

Free-form doesn't force any markup on you and thus creating your own is a first-class approach. To avoid code
duplication, shortcuts are and will be offered for various patterns, but they are just shortcut. For example, you may be
using the bootstrap-3 shortcuts to succinctly generate a [Boostrap form](http://getbootstrap.com/css/#forms) but there's
always one or two fields that just need some special treatment. Stepping outside of the Bootstrap path and going custom
with Free-form is not an afterthought but a well supported technique. For example, this won't be using an internal API
that might change without warning, it's using the same API bootstrap-3 is using and using it directly is fine. In the
future, we might want to provide some sort of [pluggable system for different styles of templates](https://github.com/carouselapps/free-form/issues/2).

Free-form doesn't handle state for you. You need to decide how to handle state. Free-form comes with a re-frame module
that helps you plug your form into the re-frame state. If you use the Reagent core you can handle state anyway you want.
Other modules could be created to handle state in different ways, for example, one could be created to have a
[private ratom the way Reagent-forms do it](https://github.com/carouselapps/free-form/issues/1).

Free-form supports but doesn't provide validation. It works very well, for example, with [Validateur](http://clojurevalidations.info/).
Nothing special has been done for this, so it should be flexible for any library. If it isn't, please,
[submit a bug report](https://github.com/carouselapps/free-form/issues/new).

The way this library works is that you write (or generate) the HTML template the way you normally do with Reagent, for
example:

```clojure
[:input {:type        :email
         :id          :email
         :placeholder "sam@example.com"}]
```

which then you pepper with special keywords to trigger the activation of inputs, labels, validation, etc. For example,
to make this input to connect to the email we would change it to:

```clojure
[:input {:free-form/input {:key :email}
         :type            :email
         :id              :email
         :placeholder     "sam@example.com"}]
```

[Reagent-forms](https://github.com/reagent-project/reagent-forms) was a big inspiration but the way it handles state was
not ideal in a Re-frame scenario.

## Current state

This library is far from complete. The focus so far was in establishing how it should work and not attempt to provide a
complete form solution. For example, there's no [support selects](https://github.com/carouselapps/free-form/issues/3) or
[text areas](https://github.com/carouselapps/free-form/issues/4) but adding them should be easy. This is likely to be
fixed soon but pull requests are welcome. Another important shortcoming is the [lack of tests](https://github.com/carouselapps/free-form/issues/6).
If you start using the library and find it's not flexible enough, please, get in touch. The goal is for it to be super
flexible and useful in a wide range of situations.

## Usage

First, you have to include Free-form in your project:

[![Clojars Project](http://clojars.org/com.carouselapps/free-form/latest-version.svg)](http://clojars.org/com.carouselapps/free-form)

To activate a form you call ```free-form.core/form``` passing the set of values to display when the form is shown for
the first time, the set of errors to display, a callback function to receive changes to the state and the form itself.
For example:

```clojure
[free-form.core/form {:email "pupeno@example.com"}
                     {:email ["Email addresses can't end in @example.com"]}
                     (fn [keys value] (println "Value for" keys "changed to" value))
 [...]]
```

Notice that it's using square brackets because it's a Reagent component. You are likely to pass the contents of ratoms
so that the form will be connected to live data, like:

```clojure
[free-form.core/form @values @errors save-state
 [...]]
```

The form is just your traditional Reagent template:

```clojure
[free-form.core/form @values @errors save-state
 [:label {:for :email} "Email"]
 [:input.form-control {:free-form/input       {:key :email}
                       :free-form/error-class {:key :text :error "error"}
                       :type                  :email
                       :id                    :email}]
 [:div.errors {:free-form/error-message {:key :email}} [:p.error]]]
```

There are three special keywords added:
* ```:free-form/input``` marks the element as being an input and the passed key is the to be used from the map of values. As an alternative, you can pass a set of keys, as in: ```{:keys [:user :email]}```, as you do with the function ```get-in```.
* ```:free-form/error-class``` will add a class if there's a validation error for the field. As with the previous one,  ```:key``` or ```:keys``` marks the field, and ```:error``` the class to be added in case of error.
* ```:free-form/error-message``` adds error messages. If there are no error messages, the surrounding element, in this case ```:div.errors``` will not be output at all. The field to be read form the map of errors is specified by  ```:key``` or ```:keys```. Lastly, the element inside this element will be used to output each of the error messages, so this might end up looking like: ```[:div.error [:p.error "Password is too short"] [:p.error "Password must contain a symbol"]]```

### Re-frame

When using Free-form with re-frame, the form is built in exactly the same way, but instead of having to code your own
state management function, you can pass the name of the event to be triggered:

```clojure
[free-form.re-frame/form @values @errors :update-state
 [...]]
```

And the library will dispatch ```[:update-state keys new-value]```. If you need to generate more involved events to
dispatch, you can pass a function that will get the keys and the new value and generate the event to be dispatched. For
example:

```clojure
[free-form.re-frame/form @values @errors (fn [keys new-value] [:update :user keys new-value])
 [...]]
```

### Bootstrap 3

You can manually generate Bootstrap 3 forms by using code such as:

```clojure
[:div.form-horizontal
 [:div.form-group {:free-form/error-class {:key :email :error "has-error"}}
  [:label.col-sm-2.control-label {:for :email} "Email"]
  [:div.col-sm-10 [:input.form-control {:free-form/input {:key :email}
                                       :type            :email
                                       :id              :email}]
   [:div.text-danger {:free-form/error-message {:key :email}} [:p]]]]]
````

but since that pattern is so common, it is now supported in this way:

```clojure
[:div.form-horizontal {:free-form/options {:mode :bootstrap-3}}
 [:free-form/field {:type        :email
                    :key         :email
                    :label       "Email"}]]
````

The ```:free-form/options {:mode :bootstrap-3}``` is what triggers Bootstrap 3 generation and Free-form will
automatically detect wether it's a [standard](http://free-form-examples.carouselapps.com/reagent/bootstrap-3), [horizontal](http://free-form-examples.carouselapps.com/reagent/bootstrap-3-horizontal)
or [inline](http://free-form-examples.carouselapps.com/reagent/bootstrap-3-inline) form.

## Changelog

### v0.2.0 - 2015-12-14
- Started Bootstrap 3 support.
- Change API from ```:free-form/input``` to ```:free-form/input```.
- Created example app to help test, exercise and develop the library: http://free-form-examples.carouselapps.com

### v0.1.1 - 2015-10-15
- Fixed a bug when dealing with errors.

### v0.1.0 - 2015-10-11
- Initial version extracted from [Ninja Tools](http://tools.screensaver.ninja).

## License

This library has been extracted from the project [Ninja Tools](http://tools.screensaver.ninja).

Copyright © 2015 Carousel Apps, Ltd. All rights reserved.

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.

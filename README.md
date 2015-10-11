# Free-form

A ClojureScript library to help building web forms with reagent and optionally re-frame. In the spirit of Clojure this
library is designed to do one task and do it well and be composable with other libraries that already exist for
performing other tasks. Reagent-forms was a big influence and inspiration but the desire to have something fit well with
re-frame drove the creation of Free-form, which can be used with or without re-frame.

Free-form doesn't handle state for you. You need to decide how to handle state. Free-form comes with a re-frame module
that helps you plug your form into the re-frame state. If you use the agnostic core you can handle state anyway you
want. Other modules could be created to handle state in different ways, for example, one could be created to have a
[private ratom the way Reagent-forms do it](https://github.com/carouselapps/free-form/issues/1).

Free-form doesn't decide the shape, that is, the HTML, of your form. In this way, you can generate or write whatever
HTML you want. This is because even if you are using a well defined style, like Bootstrap 3, there's always that one
little form that has to be different. In the future, Free-form will likely
[provide form generators](https://github.com/carouselapps/free-form/issues/2) but this is only a nice to have, not
something mandatory, so you can skip them in part or completely knowing that you are not going against the grain of this
library.

Free-form supports but doesn't provide validation. It works very well, for example, with
[Validateur](http://clojurevalidations.info/). Nothing special has been done for this, so it should be flexible for any
library. If it isn't, please, [submit a bug report](https://github.com/carouselapps/free-form/issues/new).

This library is far from completely. Notably, it doesn't yet
[support selects](https://github.com/carouselapps/free-form/issues/3) or
[text areas](https://github.com/carouselapps/free-form/issues/4). This is likely to be fixed soon but pull requests are
welcome. Another important shortcoming is the [lack of tests](https://github.com/carouselapps/free-form/issues/6). The
goal for releasing it so incomplete, so early, is to gather feedback and interest of people that need such a library as
I want it to be as flexible as possible, a tool that can be used in many different situations and scenarios, way more
than I will encounter by myself.

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
[:input {:free-form/field {:key :email}
         :type        :email
         :id          :email
         :placeholder "sam@example.com"}]
```

## Usage

First, you have to include Free-form in your project:

[![Clojars Project](http://clojars.org/com.carouselapps/free-form/latest-version.svg)](http://clojars.org/com.carouselapps/free-form)

To activate a form you call ```free-form.core/form``` passing the set of values to display when the form is shown for
the first time, the set of errors to display, a callback function to receive changes to the state and the form itself.
For example:

```clojure
[free-form.core/form {:email "pupeno@example.com"}
                     {:email "Email addresses can't end in @example.com"}
                     (fn [ks value] (println "Value for" ks "changed to" value))
 [...]]
```

Notice that it's using square brackets because it's a Reagent component. You are likely to pass the contents of ratoms
so that the form will be connected to live data, like:

```clojure
[free-form.core/form @values @errors save-state
 [...]]
```

The form is just your traditional Reagent template, in this case outputting a Bootstrap 3 form:

```clojure
[free-form.core/form @values @errors save-state
 [:div.form-horizontal
  [:div.form-group {:free-form/error-class {:key :email :error "has-error"}}
   [:label.col-sm-2.control-label {:for :email} "Email"]
   [:div.col-sm-10 [:input.form-control {:free-form/field {:key :email}
                                         :type            :email
                                         :id              :email
                                         :placeholder     "sam@example.com"}]
    [:div.text-danger {:free-form/error-message {:key :email}} [:p]]]]]]
```

There are three special keywords added:

* ```:free-form/field``` marks the element as being an input and the passed key is the to be used from the map of values.
As an alternative, you can pass a set of keys, as in: ```{:ks [:user :email]}```, as you do with the function ```get-in```.
* ```:free-form/error-class``` will add a class if there's a validation error for the field. As with the previous one,
```:key``` or ```:ks``` marks the field, and ```:error``` the class to be added in case of error.
* ```:free-form/error-message``` adds error messages. If there are no error messages, the surrounding element, in this
case ```:div.text-danger``` will not be output at all. The field to be read form the map of errors is specified by
 ```:key``` or ```:ks```. Lastly, the element inside this element will be used to output each of the error messages, so
 this might end up looking like:
```clojure
[:div.text-danger [:p "Password is too short"] [:p "Password must contain a symbol"]]
```

### re-frame

When using Free-form with re-frame, the form is built in exactly the same way, but instead of having to code your own
state management function, you can pass the name of the event to be triggered:

```clojure
[free-form.re-frame/form @values @errors :update-state
 [...]]
```

And the library will dispatch ```[:update-state ks new-value]```. If you need to generate more involved events to
dispatch, you can pass a function that will get the keys and the new value and generate the event to be dispatched. For
example:

```clojure
[free-form.re-frame/form @values @errors (fn [ks new-value] [:update :user ks ne-value])
 [...]]
```

## License

This library has been extracted from the project [Ninja Tools](http://tools.screensaver.ninja).

Copyright © 2015 Carousel Apps, Ltd. All rights reserved.

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.

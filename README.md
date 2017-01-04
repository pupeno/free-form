# Free-form

[![Code at GitHub](https://img.shields.io/badge/code-github-green.svg)](https://github.com/pupeno/free-form)
[![Clojars](https://img.shields.io/clojars/v/com.pupeno/free-form.svg)](https://clojars.org/com.pupeno/free-form)
[![Build Status](https://travis-ci.org/pupeno/free-form.svg?branch=master)](https://travis-ci.org/pupeno/free-form)

A ClojureScript library to help building web forms with [Reagent](https://reagent-project.github.io/) and optionally
[re-frame](https://github.com/Day8/re-frame) (others are welcome too). The guiding principles behind [Free-form](https://github.com/pupeno/free-form)
is that you are in control of both you data workflow as well as your markup. You can see the library in action in the
[Free-form Examples app](http://free-form-examples.pupeno.com).

Free-form doesn't force any markup on you and thus creating your own is a first-class approach. To avoid code
duplication there's an extension system that allows you to write forms in a very succinct way. A Bootstrap 3 extension
comes with Free form but adding more is not hard. One of the advantages of these mechanism is that when you have a
couple of fields that behave differently and need their own markup, you can still use a first class API and enjoy the
advantage of value handling, validation errors and everything Free form has to offer.

Free-form doesn't handle state for you. You need to decide how to handle state. Free-form comes with a re-frame module
that helps you plug your form into the re-frame state. If you use the Reagent core you can handle state anyway you want.
Other modules could be created to handle state in different ways, for example, one could be created to have a
[private ratom the way Reagent-forms do it](https://github.com/pupeno/free-form/issues/1).

Free-form supports but doesn't provide validation. It works very well, for example, with [Validateur](http://clojurevalidations.info/).
Nothing special has been done for this, so it should be flexible for any library. If it isn't, please,
[submit a bug report](https://github.com/pupeno/free-form/issues/new).

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
not ideal in a re-frame scenario.

## Current state

This library is far from complete. The focus so far was in establishing how it should work and not attempt to provide a
complete form solution. An important shortcoming is the [lack of tests](https://github.com/pupeno/free-form/issues/6).
If you start using the library and find it's not flexible enough, please, get in touch. The goal is for it to be super
flexible and useful in a wide range of situations.

## Usage

First, you have to include Free-form in your project:

[![Clojars Project](http://clojars.org/com.pupeno/free-form/latest-version.svg)](http://clojars.org/com.pupeno/free-form)

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
* ```:free-form/input``` marks the element as being an input and the passed key is to be used to connect to the value. As an alternative, you can pass a set of keys, as in: ```{:keys [:user :email]}```, as you do with the function ```get-in```.
* ```:free-form/error-class``` will add a class if there's a validation error for the field. As with the previous one,  ```:key``` or ```:keys``` marks the field, and ```:error``` the class to be added in case of error.
* ```:free-form/error-message``` adds error messages. If there are no error messages, the surrounding element, in this case ```:div.errors``` will not be output at all. The field to be read from the map of errors is specified by  ```:key``` or ```:keys```. Lastly, the element inside this element will be used to output each of the error messages, so this might end up looking like: ```[:div.error [:p.error "Password is too short"] [:p.error "Password must contain a symbol"]]```

### re-frame

When using Free-form with re-frame, the form is built in exactly the same way, but instead of having to code your own
state management function, you can pass the name of the event to be triggered:

```clojure
[free-form.re-frame/form @values @errors :update-state
 [...]]
```

And the library will dispatch ```[:update-state keys new-value]```. If you need to pass extra arguments to the handler,
specify it as a vector.

```clojure
[free-form.re-frame/form @values @errors [:update :user]
 [...]]
```

If you need to generate more involved events to
dispatch, you can pass a function that will get the keys and the new value and generate the event to be dispatched. For
example:

```clojure
[free-form.re-frame/form @values @errors (fn [keys new-value] [:update :user keys new-value])
 [...]]
```

### Extensions

There's a fourth optional argument to specify one or more extensions to be applied to the form. For example, with only
one extension called bootstrap-3:

```clojure
[free-form.core/form @values @errors save-state :bootstrap-3
 [...]]
````

or with multiple:

```clojure
[free-form.core/form @values @errors save-state [:bootstrap-3 :debug]
 [...]]
````

Extensions essentially wrap the form and thus, order is important and they can be provided more than once. For example:

```clojure
[free-form.core/form @values @errors save-state [:debug :bootstrap-3 :debug]
 [...]]
````

would help you see what the Bootstrap 3 extension is doing.

Extensions are implemented by adding a method to the multi-method free-form.extension/extension. This method will get
the name of the extension and the function that process the form. This function gets the unprocessed html markup and
returns the processed html structure. The extension should return a function that does essentially the same plus
whatever the extension wants to do. This is a system similar to middlewares found in many libraries. For example:

```clojure
(defmethod free-form.extension/extension :extension-name [_extension-name inner-fn]
  (fn [html]
    (do-something-else (inner-fn (do-something html)))))
```

do-something would pre-process the raw structure and do-something-else would post-process the structure after all inner
extensions and the main inner function have been called.

See the [debug](https://github.com/pupeno/free-form/blob/master/src/cljs/free_form/debug.cljs) and the
[Bootstrap 3 extension](https://github.com/pupeno/free-form/blob/master/src/cljs/free_form/bootsrap_3.cljs)s for
examples.

### Bootstrap 3 extension

You can manually generate Bootstrap 3 forms by using code such as:

```clojure
[free-form.core/form @values @errors save-state
 [:form.form-horizontal
  [:div.form-group {:free-form/error-class {:key :email :error "has-error"}}
   [:label.col-sm-2.control-label {:for :email} "Email"]
   [:div.col-sm-10 [:input.form-control {:free-form/input {:key :email}
                                         :type            :email
                                         :id              :email}]
    [:div.text-danger {:free-form/error-message {:key :email}} [:p]]]]]]
````

but since that pattern is so common, it is now supported by an extension:

```clojure
[free-form.core/form @values @errors save-state :bootstrap-3
 [:form.form-horizontal {:free-form/options {:mode :bootstrap-3}}
  [:free-form/field {:type  :email
                     :key   :email
                     :label "Email"}]]]
````

The extra argument, :bootstrap-3 is what triggers Bootstrap 3 generation and Free-form will automatically detect whether
it's a [standard](http://free-form-examples.pupeno.com/reagent/bootstrap-3), [horizontal](http://free-form-examples.pupeno.com/reagent/bootstrap-3-horizontal)
or [inline](http://free-form-examples.pupeno.com/reagent/bootstrap-3-inline) form.

### Debug extension

The debug extension just prints the form before and after any other processing happens. Unlike the Bootstrap 3 one, it
is not provided by default, so, you need to require the file to use it.

```clojure
(ns whatever
  (:require [free-form.core :as free-form]
            free-form.debug))
```

## Changelog

### v0.5.0
- Extension system.
- Bootstrap 3 provided as an extension.
- Debug extension.

### v0.4.2 - 2016-11-12
- Make all inputs controlled so changes can come from within our from outside.

### v0.4.1 - 2016-10-25
- Added the sources directory to the project.clj so that the library is correctly packaged.

### v0.4.0 - 2016-10-24
- Tested Free-form with re-frame 0.8.0 and Reagent 0.6.0.
- Allow marking a field as invalid when another one is invalid with :extra-keys.
- Added error messages to Bootstrap inline and horizontal forms.
- Correctly specify dependencies on Clojure and ClojureScript to avoid fixing to a single version.
- When there's no validation error, don't return the form template (invalid HTML), return nil instead.
- After a form with Bootstrap has been processed, remove the option to trigger that processing (it's invalid HTML).  
- Show a JavaScript console error if there are any Free-form leftovers after all processing is done.

### v0.3.0 - 2016-08-16
- Changed namespace from com.carouselapps to com.pupeno
- Implemented selects.
- Implemented text areas.

### v0.2.1 - 2016-08-22
- Changed the metadata of the library to point to the new namespace.

### v0.2.0 - 2015-12-14
- Started Bootstrap 3 support.
- Change API from ```:free-form/field``` to ```:free-form/input```.
- Created example app to help test, exercise and develop the library: http://free-form-examples.pupeno.com

### v0.1.1 - 2015-10-15
- Fixed a bug when dealing with errors.

### v0.1.0 - 2015-10-11
- Initial version extracted from [Ninja Tools](http://tools.screensaver.ninja).

## License

Copyright © 2015-2017 José Pablo Fernández Silva.

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.

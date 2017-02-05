;;;; Copyright © 2015-2017 José Pablo Fernández Silva

(ns free-form.core-test
  (:require [clojure.test :refer [deftest testing is]]
            [clojure.walk :refer [prewalk]]
            [free-form.core :as free-form]))

(defn- hide-on-change [form]
  (prewalk (fn [node] (if (contains? node :on-change)
                        (assoc node :on-change :was-function)
                        node))
           form))

(deftest a-test
  (let [plain-reagent-form-template [:form {:noValidate true}
                                     [:div.errors {:free-form/error-message {:key :-general}} [:p.error]]
                                     [:div.field {:free-form/error-class {:key :text :error "validation-errors"}}
                                      [:label {:for :text} "Text"]
                                      [:input.form-control {:free-form/input {:key :text}
                                                            :type            :text
                                                            :id              :text
                                                            :placeholder     "placeholder"}]
                                      [:div.errors {:free-form/error-message {:key :text}} [:p.error]]]
                                     [:div.field {:free-form/error-class {:key :email :error "validation-errors"}}
                                      [:label {:for :email} "Email"]
                                      [:input.form-control {:free-form/input {:key :email}
                                                            :type            :email
                                                            :id              :email
                                                            :placeholder     "placeholder@example.com"}]
                                      [:div.errors {:free-form/error-message {:key :email}} [:p.error]]]
                                     [:div.field {:free-form/error-class {:key :password :error "validation-errors"}}
                                      [:label {:for :password} "Password"]
                                      [:input.form-control {:free-form/input {:key :password}
                                                            :type            :password
                                                            :id              :password}]
                                      [:div.errors {:free-form/error-message {:key :password}} [:p.error]]]
                                     [:div.field {:free-form/error-class {:key :select :error "validation-errors"}}
                                      [:label {:for :select} "Select"]
                                      [:select.form-control {:free-form/input {:key :select}
                                                             :type            :select
                                                             :id              :select}
                                       [:option]
                                       [:option {:value :dog} "Dog"]
                                       [:option {:value :cat} "Cat"]
                                       [:option {:value :squirrel} "Squirrel"]
                                       [:option {:value :giraffe} "Giraffe"]]
                                      [:div.errors {:free-form/error-message {:key :select}} [:p.error]]]
                                     [:div.field {:free-form/error-class {:key :select-with-group :error "validation-errors"}}
                                      [:label {:for :select} "Select with groups"]
                                      [:select.form-control {:free-form/input {:key :select-with-group}
                                                             :type            :select
                                                             :id              :select-with-group}
                                       [:option]
                                       [:optgroup {:label "Numbers"}
                                        [:option {:value :one} "One"]
                                        [:option {:value :two} "Two"]
                                        [:option {:value :three} "Three"]
                                        [:option {:value :four} "Four"]]
                                       [:optgroup {:label "Leters"}
                                        [:option {:value :a} "A"]
                                        [:option {:value :b} "B"]
                                        [:option {:value :c} "C"]
                                        [:option {:value :d} "D"]]]
                                      [:div.errors {:free-form/error-message {:key :select-with-group}} [:p.error]]]
                                     [:div.field {:free-form/error-class {:key :textarea :error "validation-errors"}}
                                      [:label {:for :text-area} "Text area"]
                                      [:textarea.form-control {:free-form/input {:key :textarea}
                                                               :id              :textarea}]
                                      [:div.errors {:free-form/error-message {:key :textarea}} [:p.error]]]
                                     [:div.field {:free-form/error-class {:key [:t :e :x :t] :error "validation-errors"}}
                                      [:label {:for :text} "Text with deep keys"]
                                      [:input.form-control {:free-form/input {:keys [:t :e :x :t]}
                                                            :type            :text
                                                            :id              :text
                                                            :placeholder     "placeholder"}]
                                      [:div.errors {:free-form/error-message {:keys [:t :e :x :t]}} [:p.error]]]
                                     [:div.field {:free-form/error-class {:key        :text-with-extra-validation-errors :error "validation-errors"
                                                                          :extra-keys [[:text] [:-general]]}}
                                      [:label {:for :text-with-extra-validation-errors} "Text with extra validation errors"]
                                      [:input.form-control {:free-form/input {:key :text-with-extra-validation-errors}
                                                            :type            :text
                                                            :id              :text-with-extra-validation-errors
                                                            :placeholder     "This will be marked as a validation error also when Text and General have validation errors."}]
                                      [:div.errors {:free-form/error-message {:key :text-with-extra-validation-errors}} [:p.error]]]

                                     ; === Checkboxes
                                     [:div.field {:free-form/error-class {:key :checkbox-true :error "validation-errors"}}
                                      [:label {:for :checkbox-true} "Checkbox (default checked)"]
                                      [:input.form-control {:free-form/input {:key :checkbox-true}
                                                            :type            :checkbox
                                                            :id              :checkbox-true}]
                                      [:div.errors {:free-form/error-message {:key :checkbox-false}} [:p.error]]]

                                     [:div.field {:free-form/error-class {:key :checkbox-false :error "validation-errors"}}
                                      [:label {:for :checkbox-false} "Checkbox (default not checked)"]
                                      [:input.form-control {:free-form/input {:key :checkbox-false}
                                                            :type            :checkbox
                                                            :id              :checkbox-false}]
                                      [:div.errors {:free-form/error-message {:key :checkbox-false}} [:p.error]]]

                                     ; === Radio Buttons
                                     [:div.field {:free-form/error-class {:key :radio :error "validation-errors"}}
                                      [:label {:for :radio-option-1} "Radio Option 1"
                                        [:input.form-control {:free-form/input {:key :radio}
                                                              :type            :radio
                                                              :id              :radio-option-1
                                                              :value           "radio-option-1"}]]
                                      [:label {:for :radio-option-2} "Radio Option 2"
                                        [:input.form-control {:free-form/input {:key :radio}
                                                              :type            :radio
                                                              :id              :radio-option-2
                                                              :value           "radio-option-2"}]]
                                      [:label {:for :radio-option-3} "Radio Option 3"
                                        [:input.form-control {:free-form/input {:key :radio}
                                                              :type            :radio
                                                              :id              :radio-option-3
                                                              :value           "radio-option-3"}]]
                                      [:div.errors {:free-form/error-message {:key :radio}} [:p.error]]]


                                     [:button "Button"]]]

    (testing "simple generation"
      (let [generated-input (hide-on-change
                              (free-form/form {} {} (fn [_keys _value])
                                plain-reagent-form-template))]
        (is (= generated-input
               [:form {:noValidate true}
                nil
                [:div.field {}
                 [:label {:for :text} "Text"]
                 [:input.form-control {:type        :text
                                       :id          :text
                                       :placeholder "placeholder"
                                       :value       ""
                                       :on-change   :was-function}]
                 nil]
                [:div.field {}
                 [:label {:for :email} "Email"]
                 [:input.form-control {:type        :email
                                       :id          :email
                                       :placeholder "placeholder@example.com"
                                       :value       ""
                                       :on-change   :was-function}]
                 nil]
                [:div.field {}
                 [:label {:for :password} "Password"]
                 [:input.form-control {:type      :password
                                       :id        :password
                                       :value     ""
                                       :on-change :was-function}]
                 nil]
                [:div.field {}
                 [:label {:for :select} "Select"]
                 [:select.form-control {:type      :select
                                        :id        :select
                                        :value     ""
                                        :on-change :was-function}
                  [:option]
                  [:option {:value :dog} "Dog"]
                  [:option {:value :cat} "Cat"]
                  [:option {:value :squirrel} "Squirrel"]
                  [:option {:value :giraffe} "Giraffe"]]
                 nil]
                [:div.field {}
                 [:label {:for :select} "Select with groups"]
                 [:select.form-control {:type      :select
                                        :id        :select-with-group
                                        :value     ""
                                        :on-change :was-function}
                  [:option]
                  [:optgroup {:label "Numbers"}
                   [:option {:value :one} "One"]
                   [:option {:value :two} "Two"]
                   [:option {:value :three} "Three"]
                   [:option {:value :four} "Four"]]
                  [:optgroup {:label "Leters"}
                   [:option {:value :a} "A"]
                   [:option {:value :b} "B"]
                   [:option {:value :c} "C"]
                   [:option {:value :d} "D"]]]
                 nil]
                [:div.field {}
                 [:label {:for :text-area} "Text area"]
                 [:textarea.form-control {:id        :textarea
                                          :value     ""
                                          :on-change :was-function}]
                 nil]
                [:div.field {}
                 [:label {:for :text} "Text with deep keys"]
                 [:input.form-control {:type        :text
                                       :id          :text
                                       :placeholder "placeholder"
                                       :value       ""
                                       :on-change   :was-function}]
                 nil]
                [:div.field {}
                 [:label {:for :text-with-extra-validation-errors} "Text with extra validation errors"]
                 [:input.form-control {:type        :text
                                       :id          :text-with-extra-validation-errors
                                       :placeholder "This will be marked as a validation error also when Text and General have validation errors."
                                       :value       ""
                                       :on-change   :was-function}]
                 nil]

                [:div.field {}
                 [:label {:for :checkbox-true} "Checkbox (default checked)"]
                 [:input.form-control {:type           :checkbox
                                       :id             :checkbox-true
                                       :defaultChecked false
                                       :on-change      :was-function}]
                 nil]

                [:div.field {}
                 [:label {:for :checkbox-false} "Checkbox (default not checked)"]
                 [:input.form-control {:type           :checkbox
                                       :id             :checkbox-false
                                       :defaultChecked false
                                       :on-change      :was-function}]
                 nil]

                [:div.field {}
                 [:label {:for :radio-option-1} "Radio Option 1"
                   [:input.form-control {:type           :radio
                                         :id             :radio-option-1
                                         :value          "radio-option-1"
                                         :defaultChecked false
                                         :on-change      :was-function}]]
                 [:label {:for :radio-option-2} "Radio Option 2"
                   [:input.form-control {:type           :radio
                                         :id             :radio-option-2
                                         :value          "radio-option-2"
                                         :defaultChecked false
                                         :on-change      :was-function}]]
                 [:label {:for :radio-option-3} "Radio Option 3"
                   [:input.form-control {:type           :radio
                                         :id             :radio-option-3
                                         :value          "radio-option-3"
                                         :defaultChecked false
                                         :on-change      :was-function}]]
                 nil]


                [:button "Button"]]))))

    (testing "generation with initial data"
      (let [generated-input (hide-on-change
                              (free-form/form {:text     "Text value"
                                               :email    "Email value"
                                               :password "Password value"
                                               ;:select "cat" ; TODO: enable this and fix generation, as it's broken right now.
                                               ;:select-with-group "two" ; TODO: enable this and fix generation, as it's broken right now.
                                               :textarea "Textarea value"
                                               :t        {:e {:x {:t "Text with deep keys value"}}}
                                               :checkbox-true true
                                               :checkbox-false false
                                               :radio    "radio-option-2"
                                               } {} (fn [_keys _value])
                                plain-reagent-form-template))]
        (is (= generated-input
               [:form {:noValidate true}
                nil
                [:div.field {}
                 [:label {:for :text} "Text"]
                 [:input.form-control {:type        :text
                                       :id          :text
                                       :placeholder "placeholder"
                                       :value       "Text value"
                                       :on-change   :was-function}]
                 nil]
                [:div.field {}
                 [:label {:for :email} "Email"]
                 [:input.form-control {:type        :email
                                       :id          :email
                                       :placeholder "placeholder@example.com"
                                       :value       "Email value"
                                       :on-change   :was-function}]
                 nil]
                [:div.field {} [:label {:for :password} "Password"]
                 [:input.form-control {:type      :password
                                       :id        :password
                                       :value     "Password value"
                                       :on-change :was-function}]
                 nil]
                [:div.field {}
                 [:label {:for :select} "Select"]
                 [:select.form-control {:type      :select
                                        :id        :select
                                        :value     ""
                                        :on-change :was-function}
                  [:option]
                  [:option {:value :dog} "Dog"]
                  [:option {:value :cat} "Cat"]
                  [:option {:value :squirrel} "Squirrel"]
                  [:option {:value :giraffe} "Giraffe"]]
                 nil]
                [:div.field {}
                 [:label {:for :select} "Select with groups"]
                 [:select.form-control {:type      :select
                                        :id        :select-with-group
                                        :value     ""
                                        :on-change :was-function}
                  [:option]
                  [:optgroup {:label "Numbers"}
                   [:option {:value :one} "One"]
                   [:option {:value :two} "Two"]
                   [:option {:value :three} "Three"]
                   [:option {:value :four} "Four"]]
                  [:optgroup {:label "Leters"}
                   [:option {:value :a} "A"]
                   [:option {:value :b} "B"]
                   [:option {:value :c} "C"]
                   [:option {:value :d} "D"]]]
                 nil]
                [:div.field {}
                 [:label {:for :text-area} "Text area"]
                 [:textarea.form-control {:id        :textarea
                                          :value     "Textarea value"
                                          :on-change :was-function}]
                 nil]
                [:div.field {}
                 [:label {:for :text} "Text with deep keys"]
                 [:input.form-control {:type        :text
                                       :id          :text
                                       :placeholder "placeholder"
                                       :value       "Text with deep keys value"
                                       :on-change   :was-function}]
                 nil]
                [:div.field {}
                 [:label {:for :text-with-extra-validation-errors} "Text with extra validation errors"]
                 [:input.form-control {:type        :text
                                       :id          :text-with-extra-validation-errors
                                       :placeholder "This will be marked as a validation error also when Text and General have validation errors."
                                       :value       ""
                                       :on-change   :was-function}]
                 nil]

                [:div.field {}
                 [:label {:for :checkbox-true} "Checkbox (default checked)"]
                 [:input.form-control {:type           :checkbox
                                       :id             :checkbox-true
                                       :defaultChecked true
                                       :on-change      :was-function}]
                 nil]

                [:div.field {}
                 [:label {:for :checkbox-false} "Checkbox (default not checked)"]
                 [:input.form-control {:type           :checkbox
                                       :id             :checkbox-false
                                       :defaultChecked false
                                       :on-change      :was-function}]
                 nil]

                [:div.field {}
                 [:label {:for :radio-option-1} "Radio Option 1"
                   [:input.form-control {:type           :radio
                                         :id             :radio-option-1
                                         :value          "radio-option-1"
                                         :defaultChecked false
                                         :on-change      :was-function}]]
                 [:label {:for :radio-option-2} "Radio Option 2"
                   [:input.form-control {:type           :radio
                                         :id             :radio-option-2
                                         :value          "radio-option-2"
                                         :defaultChecked true
                                         :on-change      :was-function}]]
                 [:label {:for :radio-option-3} "Radio Option 3"
                   [:input.form-control {:type           :radio
                                         :id             :radio-option-3
                                         :value          "radio-option-3"
                                         :defaultChecked false
                                         :on-change      :was-function}]]
                 nil]

                [:button "Button"]]))))))

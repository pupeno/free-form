;;;; Copyright © 2017 José Pablo Fernández Silva

(ns free-form.reagent-toolbox
  (:require [clojure.walk :refer [postwalk prewalk]]
            [clojure.string :as s]
            [reagent-toolbox.core :as rt]
            [free-form.util :refer [field? key->keys attributes-index]]
            [free-form.extension :as extension]))

(defn- expand-reagent-toolbox-input [id label keys type options extra-validation-error-keys]
  (case type
    :select [rt/dropdown {:label           label
                          :source          (flatten (map (fn [[value label]]
                                                           (if (sequential? label)
                                                             (map (fn [[sub-value sub-label]]
                                                                    {:label (str value ": " sub-label) :value sub-value})
                                                                  (partition 2 label))
                                                             {:label label :value value}))
                                                         (partition 2 options)))
                          :free-form/input {:keys             keys
                                            :error-on         :error
                                            :extra-error-keys extra-validation-error-keys}}]
    :textarea [rt/input {:label           label
                         :free-form/input {:keys             keys
                                           :error-on         :error
                                           :extra-error-keys extra-validation-error-keys}
                         :id              id
                         :multiline       true}]
    :checkbox [rt/checkbox {:label           label
                            :free-form/input {:keys             keys
                                              :value-on         :checked
                                              :blank-value      false
                                              :error-on         :error
                                              :extra-error-keys extra-validation-error-keys}}]
    :switch [rt/switch {:label           label
                        :free-form/input {:keys             keys
                                          :value-on         :checked
                                          :blank-value      false
                                          :error-on         :error
                                          :extra-error-keys extra-validation-error-keys}}]
    :radio [rt/radio-group (map (fn [[value label]]         ; TODO: this is not finished as radio buttons are not behaving great yet. Missing setting value, showing the group label, displaying options only once, etc. Errors!
                                  ^{:key (str "free-form-reagent-toolbox-radio-button-" value "-" label)}
                                  [rt/radio-button {:label label :value value}])
                                (partition 2 options))]
    [rt/input {:label           label
               :free-form/input {:keys             keys
                                 :error-on         :error
                                 :extra-error-keys extra-validation-error-keys}
               :type            type
               :id              id}]))

(defn- expand-reagent-toolbox-fields [node]
  (if (field? node)
    (let [{:keys [type keys extra-validation-error-keys label options]} (key->keys (second node))
          id (s/join "-" (map name keys))]
      (expand-reagent-toolbox-input id label keys type options extra-validation-error-keys))
    node))

(defn- expand-reagent-toolbox-form [node]
  (postwalk expand-reagent-toolbox-fields node))

(defmethod extension/extension :reagent-toolbox [_extension-name inner-fn]
  (fn [html]
    (inner-fn (prewalk expand-reagent-toolbox-form html))))

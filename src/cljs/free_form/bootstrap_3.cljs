;;;; Copyright © 2017 José Pablo Fernández Silva

(ns free-form.bootstrap-3
  (:require [clojure.walk :refer [postwalk prewalk]]
            [clojure.string :as s]
            [free-form.util :refer [field? key->keys attributes-index]]
            [free-form.extension :as extension]))

(defn- expand-bootstrap-3-input [id keys type placeholder options]
  (case type
    :select [:select.form-control {:free-form/input {:keys keys}
                                   :type            type
                                   :id              id
                                   :placeholder     placeholder}
             (letfn [(generate-option [[value name]]
                       (if (sequential? name)
                         ^{:key value} [:optgroup {:label value}
                                        (map generate-option (partition 2 name))]
                         ^{:key value} [:option {:value value} name]))]
               (map generate-option (partition 2 options)))]
    :textarea [:textarea.form-control {:free-form/input {:keys keys}
                                       :type            type
                                       :id              id}]
    :checkbox [:input {:free-form/input {:keys keys}
                       :type            type
                       :id              id}]
    :radio (map (fn [[value name]]
                  ^{:name name}
                  [:input {:free-form/input {:keys keys}
                           :type            type
                           :name            id
                           :value           value}])
                (partition 2 options))
    [:input.form-control {:free-form/input {:keys keys}
                          :type            type
                          :id              id
                          :placeholder     placeholder}]))

(defn- expand-bootstrap-3-fields [node]
  (if (field? node)
    (let [{:keys [type keys extra-validation-error-keys label placeholder options]} (key->keys (second node))
          id (s/join "-" (map name keys))]
      (case type
        :checkbox [:div.checkbox {:free-form/error-class {:keys       keys
                                                          :extra-keys extra-validation-error-keys
                                                          :error      "has-error"}}
                   [:label (expand-bootstrap-3-input id keys type placeholder options) label]
                   [:div.text-danger {:free-form/error-message {:keys keys}} [:p]]]
        :radio [:div {:free-form/error-class {:keys       keys
                                              :extra-keys extra-validation-error-keys
                                              :error      "has-error"}}
                [:label label]
                (map (fn [input]
                       ^{:key (str id "-" (get-in input [1 :value]))}
                       [:div.radio
                        [:label input (:name (meta input))]])
                     (expand-bootstrap-3-input id keys type placeholder options))
                [:div.text-danger {:free-form/error-message {:keys keys}} [:p]]]
        [:div.form-group {:free-form/error-class {:keys keys :extra-keys extra-validation-error-keys :error "has-error"}}
         [:label.control-label {:for id} label]
         (expand-bootstrap-3-input id keys type placeholder options)
         [:div.text-danger {:free-form/error-message {:keys keys}} [:p]]]))
    node))

(defn- expand-bootstrap-3-horizontal-fields [node]
  (if (field? node)
    (let [{:keys [type keys extra-validation-error-keys label placeholder options]} (key->keys (second node))
          id (s/join "-" (map name keys))]
      (case type
        :checkbox [:div.form-group {:free-form/error-class {:keys       keys
                                                            :extra-keys extra-validation-error-keys
                                                            :error      "has-error"}}
                   [:div.col-sm-offset-2.col-sm-10
                    [:div.checkbox
                     [:label (expand-bootstrap-3-input id keys type placeholder options) label]]
                    [:div.text-danger {:free-form/error-message {:keys keys}} [:p]]]]
        :radio [:div.form-group {:free-form/error-class {:keys       keys
                                                         :extra-keys extra-validation-error-keys
                                                         :error      "has-error"}}
                [:label.col-sm-2.control-label label]
                (-> [:div.col-sm-10]
                    (into (map (fn [input]
                                 ^{:key (str id "-" (get-in input [1 :value]))}
                                 [:div.radio
                                  [:label input (:name (meta input))]])
                               (expand-bootstrap-3-input id keys type placeholder options)))
                    (conj [:div.text-danger {:free-form/error-message {:keys keys}} [:p]]))]
        [:div.form-group {:free-form/error-class {:keys       keys
                                                  :extra-keys extra-validation-error-keys
                                                  :error      "has-error"}}
         [:label.col-sm-2.control-label {:for id} label]
         [:div.col-sm-10 (expand-bootstrap-3-input id keys type placeholder options)
          [:div.text-danger {:free-form/error-message {:keys keys}} [:p]]]]))
    node))

(defn- expand-bootstrap-3-inline-fields [node]
  (if (field? node)
    (let [{:keys [type keys extra-validation-error-keys label placeholder options]} (key->keys (second node))
          id (s/join "-" (map name keys))]
      (case type
        :checkbox [:div.form-group
                   [:label.checkbox-inline {:free-form/error-class {:keys       keys
                                                                    :extra-keys extra-validation-error-keys
                                                                    :error      "has-error"}}
                    (expand-bootstrap-3-input id keys type placeholder options) label]
                   [:div.text-danger {:free-form/error-message {:keys keys}} [:p]]]
        :radio [:div.form-group {:free-form/error-class {:keys       keys
                                                         :extra-keys extra-validation-error-keys
                                                         :error      "has-error"}}
                (when label [:label label])
                (map (fn [input]
                       ^{:key (str id "-" (get-in input [1 :value]))}
                       [:div.radio-inline
                        [:label input (:name (meta input))]])
                     (expand-bootstrap-3-input id keys type placeholder options))
                [:div.text-danger {:free-form/error-message {:keys keys}} [:p]]]
        [:div.form-group {:free-form/error-class {:keys       keys
                                                  :extra-keys extra-validation-error-keys
                                                  :error      "has-error"}}
         [:label.control-label {:for id} label]
         " "
         (expand-bootstrap-3-input id keys type placeholder options)
         " "
         [:div.text-danger {:free-form/error-message {:keys keys}} [:p]]]))
    node))

(defn- bootstrap-3-form-horizontal? [node]
  (and (coll? node)
       (= :form.form-horizontal (first node))))

(defn- bootstrap-3-form-inline? [node]
  (and (coll? node)
       (= :form.form-inline (first node))))

(defn- expand-bootstrap-3-form [node]
  (cond (bootstrap-3-form-horizontal? node) (postwalk expand-bootstrap-3-horizontal-fields node)
        (bootstrap-3-form-inline? node) (postwalk expand-bootstrap-3-inline-fields node)
        :else (postwalk expand-bootstrap-3-fields node)))

(defmethod extension/extension :bootstrap-3 [_extension-name inner-fn]
  (fn [html]
    (inner-fn (prewalk expand-bootstrap-3-form html))))

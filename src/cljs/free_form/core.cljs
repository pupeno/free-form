;;;; Copyright © 2015 Carousel Apps, Ltd. All rights reserved.

(ns free-form.core
  (:require [clojure.walk :refer [postwalk]]))

(def ^:private attributes-index 1)                          ; The second element in structure that represents an input is the attributes, as in :type, :key, etc.

(defn- extract-attributes [node key]
  (let [attributes (get node attributes-index)
        re-attributes (key attributes)
        attributes (dissoc attributes key)
        ks (or (:ks re-attributes) [(:key re-attributes)])]
    [attributes re-attributes ks]))

(defn- field? [node]
  (and (coll? node)
       (contains? (second node) :free-form/field)))

(defn- js-event-value [event]
  (.-value (.-target event)))

(defn- bind-field [values on-change node]
  (if (not (field? node))
    node
    (let [[attributes _ ks] (extract-attributes node :free-form/field)]
      (assoc node attributes-index (assoc attributes :default-value (get-in values ks)
                                                     :on-change #(on-change ks (js-event-value %)))))))

(defn- error-class?
  "Tests whether the node should be marked with an error class should the field have an associated error."
  [node]
  (and (coll? node)
       (contains? (second node) :free-form/error-class)))

(defn- bind-error-class [errors node]
  (if (not (error-class? node))
    node
    (let [[attributes re-attributes ks] (extract-attributes node :free-form/error-class)]
      (assoc node attributes-index
                  (if (nil? (get-in errors ks))
                    attributes
                    (update attributes :class #(str (or (:error re-attributes) "error") %)))))))

(defn- error-messages?
  [node]
  (and (coll? node)
       (contains? (second node) :free-form/error-message)))

(defn- bind-error-messages [errors node]
  (if (not (error-messages? node))
    node
    (let [[attributes _ ks] (extract-attributes node :free-form/error-message)]
      (if-let [errors (get-in errors ks)]
        (vec (concat
               (drop-last (assoc node attributes-index attributes))
               (map #(conj (get node 2) %) errors)))
        node))))

(defn form [values errors on-change form]
  (let [errors (or errors {})]
    (postwalk (fn [node]
                (->> node
                     (bind-field values on-change)
                     (bind-error-class errors)
                     (bind-error-messages errors)))
              form)))

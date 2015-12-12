;;;; Copyright © 2015 Carousel Apps, Ltd. All rights reserved.

(ns free-form.core
  (:require [clojure.walk :refer [postwalk prewalk]]))

(def ^:private attributes-index 1)                          ; The second element in structure that represents an input is the attributes, as in :type, :key, etc.

(defn- extract-attributes [node key]
  (let [attributes (get node attributes-index)
        re-attributes (key attributes)
        attributes (dissoc attributes key)
        keys (or (:keys re-attributes) [(:key re-attributes)])]
    [attributes re-attributes keys]))

(defn- input? [node]
  (and (coll? node)
       (contains? (second node) :free-form/input)))

(defn- js-event-value [event]
  (.-value (.-target event)))

(defn- bind-input [values on-change node]
  (if (not (input? node))
    node
    (let [[attributes _ keys] (extract-attributes node :free-form/input)]
      (assoc node attributes-index (assoc attributes :default-value (get-in values keys)
                                                     :on-change #(on-change keys (js-event-value %)))))))

(defn- error-class?
  "Tests whether the node should be marked with an error class should the field have an associated error."
  [node]
  (and (coll? node)
       (contains? (second node) :free-form/error-class)))

(defn- bind-error-class [errors node]
  (if (not (error-class? node))
    node
    (let [[attributes re-attributes keys] (extract-attributes node :free-form/error-class)]
      (assoc node attributes-index
                  (if (nil? (get-in errors keys))
                    attributes
                    (update attributes :class #(str (or (:error re-attributes) "error") %)))))))

(defn- error-messages?
  [node]
  (and (coll? node)
       (contains? (second node) :free-form/error-message)))

(defn- bind-error-messages [errors node]
  (if (not (error-messages? node))
    node
    (let [[attributes _ keys] (extract-attributes node :free-form/error-message)]
      (if-let [errors (get-in errors keys)]
        (vec (concat
               (drop-last (assoc node attributes-index attributes))
               (map #(conj (get node 2) %) errors)))
        node))))

(defn key->keys [m]
  (if (contains? m :key)
    (if (contains? m :keys)
      (throw (js/Error. "key->keys expects a map with :key or :keys, not both"))
      (assoc m :keys [(:key m)]))
    m))

(defn- bootstrap-horizontal-fields? [node]
  (and (coll? node) (= :free-form/field (first node))))

(defn- expand-bootstrap-horizontal-fields [node]
  (if (bootstrap-horizontal-fields? node)
    (let [{:keys [type keys label placeholder]} (key->keys (second node))
          id (clojure.string/join "-" (map name keys))]
      [:div.form-group {:free-form/error-class {:keys keys :error "has-error"}}
       [:label.col-sm-2.control-label {:for id} label]
       [:div.col-sm-10 [:input.form-control {:free-form/input {:keys keys}
                                             :type            type
                                             :id              id
                                             :placeholder     placeholder}]
        [:div.text-danger {:free-form/error-message {:keys keys}} [:p]]]])
    node))

(defn- expand-bootstrap-fields [node]
  (throw (js/Error. "expand-bootstrap-fields not implemented yet")))

(defn- expand-bootstrap-fields-inline [node]
  (throw (js/Error. "expand-bootstrap-fields-inline not implemented yet")))

(defn- bootstrap-form? [node]
  (and (coll? node)
       (= :form (first node))))

(defn- bootstrap-form-horizontal? [node]
  (and (coll? node)
       (= :form.form-horizontal (first node))))

(defn- bootstrap-form-inline? [node]
  (and (coll? node)
       (= :form.form-inline (first node))))

(defn- expand-bootstrap-form [node]
  (cond (bootstrap-form? node) (postwalk expand-bootstrap-fields node)
        (bootstrap-form-horizontal? node) (postwalk expand-bootstrap-horizontal-fields node)
        (bootstrap-form-inline? node) (postwalk expand-bootstrap-fields-inline node)
        :else node))

(defn form [values errors on-change form]
  (let [errors (or errors {})]
    (->> form
         (prewalk expand-bootstrap-form)
         (postwalk #(bind-input values on-change %))
         (postwalk #(bind-error-class errors %))
         (postwalk #(bind-error-messages errors %)))))

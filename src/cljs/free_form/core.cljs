;;;; Copyright © 2015-2017 José Pablo Fernández Silva

(ns free-form.core
  (:require free-form.bootstrap-3                           ; Just to make the bootstrap-3 extension automatically available
            free-form.debug                                 ; Just to make the debug extension automatically available
            clojure.string
            [clojure.walk :refer [postwalk prewalk]]
            [free-form.extension :as extension]
            [free-form.util :refer [field? key->keys attributes-index]]))

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
      (assoc node attributes-index (assoc attributes :value (or (get-in values keys) "")
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
                  (if (not-any? #(get-in errors %) (conj (:extra-keys re-attributes) keys))
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
        nil))))

(defn- warn-of-leftovers [node]
  (let [attrs (get node attributes-index)]
    (when (and (map? attrs)
               (some #(= "free-form" (namespace %)) (keys attrs)))
      (js/console.error "There are free-form-looking leftovers on" (pr-str node))))
  node)

(defn form
  ([values errors on-change html]
   (form values errors on-change [] html))
  ([values errors on-change extensions html]
   (let [errors (or errors {})
         extensions (if (sequential? extensions) extensions [extensions])
         inner-fn (fn [html]
                            (->> html
                                 (postwalk #(bind-input values on-change %))
                                 (postwalk #(bind-error-class errors %))
                                 (postwalk #(bind-error-messages errors %))))]
     (postwalk #(warn-of-leftovers %)
               ((reduce #(extension/extension %2 %1) inner-fn extensions) html)))))

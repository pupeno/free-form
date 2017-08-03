;;;; Copyright © 2015-2017 José Pablo Fernández Silva

(ns free-form.core
  (:require clojure.string
            [clojure.walk :refer [postwalk prewalk]]
            [free-form.extension :as extension]
            [free-form.util :refer [field? key->keys attributes-index]]))

(defn- extract-attributes [node key]
  (let [attributes    (get node attributes-index)
        re-attributes (key attributes)
        attributes    (dissoc attributes key)
        keys          (or (:keys re-attributes) [(:key re-attributes)])]
    [attributes re-attributes keys]))

(defn- input? [node]
  (and (coll? node)
       (contains? (second node) :free-form/input)))

(defn- js-event-value [event]
  (let [target (.-target event)]
    (case (.-type target)
      "checkbox" (.-checked target)
      (.-value target))))

(defn- extract-event-value [event]
  (if (or (boolean? event)
          (string? event))
    event                                                                                                               ; React-toolbox generates events that already contain a stracted string of the value as the first paramenter
    (js-event-value event)))                                                                                            ; for all other cases, we extract it ourselves.

(defn- first-non-nil [& coll]
  (first (filter (complement nil?) coll)))

(defn- bind-input [values errors on-change node]
  (if (not (input? node))
    node
    (let [[attributes free-form-attributes keys] (extract-attributes node :free-form/input)
          {:keys [value-on error-on extra-error-keys]} free-form-attributes
          on-change-fn #(on-change keys (extract-event-value %1))
          value-on     (or value-on (case (:type attributes)
                                      (:checkbox :radio) :default-checked
                                      :value))
          value        (case (:type attributes)
                         :checkbox (= true (get-in values keys))
                         :radio (= (:value attributes) (get-in values keys))
                         (first-non-nil (get-in values keys) (:blank-value free-form-attributes) ""))
          input-errors (get-in errors keys)]
      (assoc node attributes-index
                  (cond-> attributes
                          true (assoc :on-change on-change-fn)
                          true (assoc value-on value)
                          (and error-on input-errors) (assoc error-on (clojure.string/join " " input-errors))
                          (and extra-error-keys (some #(get-in errors %) extra-error-keys)) (assoc error-on " "))))))

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
   (let [errors     (or errors {})
         extensions (if (sequential? extensions) extensions [extensions])
         inner-fn   (fn [html]
                      (->> html
                           (postwalk #(bind-input values errors on-change %))
                           (postwalk #(bind-error-class errors %))
                           (postwalk #(bind-error-messages errors %))))]
     (postwalk #(warn-of-leftovers %)
               ((reduce #(extension/extension %2 %1) inner-fn extensions) html)))))

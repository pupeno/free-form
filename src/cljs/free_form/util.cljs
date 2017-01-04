;;;; Copyright © 2017 José Pablo Fernández Silva

(ns free-form.util)

(defn field? [node]
  (and (coll? node) (= :free-form/field (first node))))

(defn key->keys [m]
  (if (contains? m :key)
    (if (contains? m :keys)
      (throw (js/Error. "key->keys expects a map with :key or :keys, not both"))
      (assoc m :keys [(:key m)]))
    m))

(def attributes-index
  "The second element in structure that represents an input is the attributes, as in :type, :key, etc."
  1)

; Not needed yet, but might be needed in the future
#_(defn- remove-free-form-attribute [node attr-location attr-name]
    (let [node (update-in node [attributes-index attr-location] dissoc attr-name)]
      (if (empty? (get-in node [attributes-index attr-location]))
        (update-in node [attributes-index] dissoc attr-location)
        node)))

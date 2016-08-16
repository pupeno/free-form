;;;; Copyright © 2015, 2016 José Pablo Fernández Silva, All rights reserved.

(ns free-form.re-frame
  (:require [free-form.core :as core]
            [re-frame.core :as re-frame]))

(defn form [values errors event form]
  (let [re-frame-event-generator
        (fn [ks value]
          (let [event-v (cond
                          (fn? event)     (event ks value)
                          (vector? event) (conj event ks value)
                          :else           [event ks value])]
            (re-frame/dispatch event-v)))]
    [core/form values errors re-frame-event-generator form]))

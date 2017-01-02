;;;; Copyright © 2015-2017 José Pablo Fernández Silva. All rights reserved.

(ns free-form.re-frame
  (:require [free-form.core :as core]
            [re-frame.core :as re-frame]))

(defn form [values errors event form]
  (let [re-frame-event-generator
        (fn [keys value]
          (let [event-v (cond
                          (fn? event) (event keys value)
                          (vector? event) (conj event keys value)
                          :else [event keys value])]
            (re-frame/dispatch event-v)))]
    [core/form values errors re-frame-event-generator form]))

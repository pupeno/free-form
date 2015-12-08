;;;; Copyright © 2015 Carousel Apps, Ltd. All rights reserved.

(ns free-form.re-frame
  (:require [free-form.core :as core]
            [re-frame.core :as re-frame]))

(defn form [values errors event form]
  (let [re-frame-event-generator
        (fn [ks value]
          (let [event-v (if (fn? event)
                          (event ks value)
                          [event ks value])]
            (re-frame/dispatch event-v)))]
    [core/form values errors re-frame-event-generator form]))

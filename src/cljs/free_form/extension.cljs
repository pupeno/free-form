;;;; Copyright © 2017 José Pablo Fernández Silva. All rights reserved.

(ns free-form.extension)

(defmulti extension (fn [extension-name _inner-fn] extension-name))


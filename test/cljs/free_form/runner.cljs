;;;; Copyright © 2016-2017 José Pablo Fernández Silva. All rights reserved.

(ns free-form.runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [free-form.core-test]))

(doo-tests 'free-form.core-test)

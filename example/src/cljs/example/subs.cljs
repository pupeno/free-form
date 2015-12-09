(ns example.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame]))

(re-frame/register-sub
  :re-frame-bootstrap
  (fn [db]
    (reaction (:re-frame-bootstrap @db))))

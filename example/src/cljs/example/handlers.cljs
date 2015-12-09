(ns example.handlers
    (:require [re-frame.core :as re-frame]
              [example.db :as db]))

(re-frame/register-handler
 :initialize-db
 (fn  [_ _]
   db/default-db))

(re-frame/register-handler
  :update-re-frame-bootstrap
  (fn [db args]
    (println args)
    db))

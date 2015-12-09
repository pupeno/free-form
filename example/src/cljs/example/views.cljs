(ns example.views
  (:require [re-frame.core :as re-frame]
            [free-form.core :as ff-core]
            [free-form.re-frame :as ff-re-frame]))

(defn main-panel []
  (let [re-frame-bootstrap (re-frame/subscribe [:re-frame-bootstrap])]
    (fn []
      [:div
       [:h1 "Re-frame + Bootstrap"]
       [ff-re-frame/form {} {} :update-re-frame-bootstrap
        [:form.form-horizontal {:novalidate true
                                #_:free-form/renderer #_{:name        :bootstrap
                                                         :label-width 2
                                                         :value-width 10}}
         [:div.col-sm-offset-2.col-sm-10 {:free-form/error-message {:key :-general}} [:p.text-danger]]
         [:free-form/field {:type        :text
                            :key         :text
                            :label       "Text"
                            :placeholder "placeholder"}]
         [:free-form/field {:type        :email
                            :key         :email
                            :label       "Email"
                            :placeholder "placeholder@example.com"}]
         [:free-form/field {:type  :password
                            :label "Password"
                            :ks    [:password]}]
         [:div.form-group
          [:div.col-sm-offset-2.col-sm-5
           [:button.btn.btn-primary {:type :submit}
            "Button"]]]]]])))

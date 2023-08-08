(ns app.subs
  (:require [re-frame.core :as rf]
            [emmy.env :as emmy]
            [emmy.matrix :as matrix]))

(rf/reg-sub :app/todos
            (fn [db _]
              (:todos db)))

(rf/reg-sub :app/springs
            (fn [db _]
              (:springs db)))

(rf/reg-sub :app/degrees
            :<- [:app/springs]
            (fn [springs _]
              (matrix/diagonal springs)))

(rf/reg-sub :app/adjacencies
            :<= [:app/springs]
            :<= [:app/degrees]
            (fn [[springs degrees] _]
               (emmy/- springs degrees)))

(comment
  (let [springs (rf/subscribe [:app/springs])]
    (matrix/diagonal @springs))

  (let [degrees (rf/subscribe [:app/degrees])]
    @degrees)


  (+ (matrix/I 2))

  )
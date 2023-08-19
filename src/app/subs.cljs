(ns app.subs
  (:require [re-frame.core :as rf]
            [emmy.env :as emmy]
            [emmy.matrix :as matrix]))

(rf/reg-sub :app/db
            (fn [db _]
              db))

(rf/reg-sub :app/todos
            (fn [db _]
              (:todos db)))

(rf/reg-sub :app/app-state
            (fn [db _]
              (:app-state db)))

(rf/reg-sub :app/springs
            :<- [:app/app-state]
            (fn [app-state _]
              (apply matrix/by-rows (:springs app-state))))

(rf/reg-sub :app/displacements
            :<- [:app/app-state]
            (fn [app-state _]
              (apply matrix/by-rows (:displacement app-state))))

(rf/reg-sub :app/degrees
            :<- [:app/springs]
            (fn [springs _]
              (matrix/diagonal springs)))

(rf/reg-sub :app/forces
            :<- [:app/springs]
            :<- [:app/displacements]
            (fn [[springs displacements] _]
              (emmy/- (emmy/* springs (matrix/transpose displacements)))))

(rf/reg-sub :app/displacement
            :<- [:app/displacements]
            (fn [displacements [_ i]]
              (get-in displacements [0 i])))

(rf/reg-sub :app/force
            :<- [:app/forces]
            (fn [forces [_ i]]
              (get-in forces [i 0])))


(comment
  (let [
        degrees (rf/subscribe [:app/degrees])
        springs (rf/subscribe [:app/springs])
        displacements (rf/subscribe [:app/displacements])
        displacement (rf/subscribe [:app/displacement 1])
        forces (rf/subscribe [:app/forces])
        force (rf/subscribe [:app/force 1])
        elastic-thinning (rf/subscribe [:app/elastic-thinning 0])]
    @elastic-thinning)
  
  
  )
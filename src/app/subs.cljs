(ns app.subs
  (:require [re-frame.core :as rf]
            [emmy.env :as emmy]
            [emmy.matrix :as matrix]))

(def dampening-factor 0.1)

(defn calculate-forces
  [springs displacements]
  (emmy/- (emmy/* springs
                  displacements)))

(defn calculate-displacements 
  [displacements forces]
  (emmy/+ displacements 
          (emmy/* dampening-factor forces)))

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

(rf/reg-sub :app/initial-displacements
            :<- [:app/app-state]
            (fn [app-state _]
              (apply matrix/by-rows (:displacement app-state))))

(rf/reg-sub :app/time
            :<- [:app/app-state]
            (fn [app-state _]
              (:time app-state)))

(rf/reg-sub :app/forces
            :<- [:app/springs]
            :<- [:app/initial-displacements]
            (fn [[springs displacements] _]
              (emmy/- (emmy/* springs displacements))))

(rf/reg-sub :app/initial-displacement
            :<- [:app/initial-displacements]
            (fn [displacements [_ i]]
              (get-in displacements [i 0])))

(rf/reg-sub :app/force
            :<- [:app/forces]
            (fn [forces [_ i]]
              (get-in forces [i 0])))

(rf/reg-sub :app/simulation-displacements
            :<- [:app/springs]
            :<- [:app/initial-displacements]
            :<- [:app/time]
            (fn [[springs displacements time] _]
              (nth (iterate (fn [displacements]
                                 (let [forces (calculate-forces springs displacements)
                                       new-displacements (calculate-displacements displacements forces)]
                                   new-displacements))
                                 displacements) time)))

(rf/reg-sub :app/simulation-displacement
            :<- [:app/simulation-displacements]
            (fn [simulation-displacements [_ i]]
              (get-in simulation-displacements [i 0])))

(rf/reg-sub :app/simulation-forces
            :<- [:app/springs]
            :<- [:app/simulation-displacements]
            (fn [[springs simulation-displacements] _]
              (calculate-forces springs simulation-displacements)))


(rf/reg-sub :app/simulation-force
            :<- [:app/simulation-forces]
            (fn [simulation-forces [_ i]]
              (get-in simulation-forces [i 0])))

(comment
  (let [d (rf/subscribe [:app/simulation-displacement 1])]
       d)

  )
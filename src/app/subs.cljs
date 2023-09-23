(ns app.subs
  (:require [re-frame.core :as rf]
            [emmy.env :as emmy]
            [emmy.matrix :as matrix]))

(def dampening-factor 0.1)

(defn calculate-forces
  [springs displacements]
  (emmy/- (emmy/* springs 
                  (matrix/transpose displacements))))

(defn calculate-displacements 
  [displacements forces]
  (emmy/+ (matrix/transpose displacements) 
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
              (emmy/- (emmy/* springs (matrix/transpose displacements)))))

(rf/reg-sub :app/initial-displacement
            :<- [:app/initial-displacements]
            (fn [displacements [_ i]]
              (get-in displacements [0 i])))

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
                                    (matrix/transpose new-displacements)))
                                 displacements) time)))

(rf/reg-sub :app/simulation-displacement
            :<- [:app/simulation-displacements]
            (fn [simulation-displacements [_ i]]
              (get-in simulation-displacements [0 i])))

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

  (let [sf (rf/subscribe [:app/initial-displacement 1])]
    sf)

  (let [sf (rf/subscribe [:app/simulation-displacement 1])]
    sf)

  (let [springs       (rf/subscribe [:app/springs])
        displacements (rf/subscribe [:app/initial-displacements])
        forces        (calculate-forces @springs @displacements)]
    (emmy/* dampening-factor forces))


  (let [springs       (rf/subscribe [:app/springs])
        displacements (rf/subscribe [:app/initial-displacements])]
    (take 5 (iterate (fn [displacements]
                       (let [forces (calculate-forces @springs displacements)
                             new-displacements (calculate-displacements displacements forces)]
                         (matrix/transpose new-displacements)))
                     @displacements)))

  (let [sd (rf/subscribe [:app/simulation-displacements])]
    (get-in @sd [0 1]))
  )
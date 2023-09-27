(ns app.core
    (:require
     [cljs.spec.alpha :as s]
     [uix.core :as uix :refer [defui $]]
     [uix.dom]
     [app.hooks :as hooks]
     [app.subs]
     [app.handlers]
     [app.fx]
     [app.db]
     [re-frame.core :as rf]
     [clojure.string :as str]
     [goog.string :as gs]
     [goog.string.format]))

(defn color-fn [force]
  (cond (<= (abs force) 0) "#ffffff"
        (<= (abs force) 1) "#ffe6e6"
        (<= (abs force) 2) "#ffcccc"
        (<= (abs force) 3) "#ffb3b3"
        (<= (abs force) 4) "#ff9999"
        (<= (abs force) 5) "#ff8080"
        (<= (abs force) 6) "#ff6666"
        (<= (abs force) 7) "#ff4d4d"
        (<= (abs force) 8) "#ff3333"
        (<= (abs force) 9) "#ff1a1a"
        (<= (abs force) 10) "#ff0000"
        (<= (abs force) 11) "#e60000"
        (<= (abs force) 12) "#cc0000"
        (<= (abs force) 13) "#b30000"
        (<= (abs force) 14) "#990000"
        (<= (abs force) 15) "#800000"
        (<= (abs force) 16) "#660000"
        (<= (abs force) 17) "#4d0000"
        (<= (abs force) 18) "#330000"
        (<= (abs force) 19) "#1a0000"
        :else "#1a0000"))

(defui spring-network-view []
  (let [
        x1 (hooks/use-subscribe [:app/simulation-displacement 0])
        x2 (hooks/use-subscribe [:app/simulation-displacement 1])
        x3 (hooks/use-subscribe [:app/simulation-displacement 2])
        f1 (hooks/use-subscribe [:app/simulation-force 0])
        f2 (hooks/use-subscribe [:app/simulation-force 1])
        f3 (hooks/use-subscribe [:app/simulation-force 2])
        ]
    ($ :svg
       ($ :path {:stroke "black"
                 :stroke-width 10
                 :d (str/join " " ["M" (+ 100 x1) 35 "L" (+ 250 x2) 35])})
       ($ :path {:stroke "black"
                 :stroke-width 10
                 :d (str/join " " ["M" (+ 250 x2) 35 "L" (+ 350 x3) 35])})
       ($ :path {:d "M 125 0 L 125 5" :stroke "black" :stroke-width 5})
       ($ :path {:d "M 225 0 L 225 5" :stroke "black" :stroke-width 5})
       ($ :path {:d "M 325 0 L 325 5" :stroke "black" :stroke-width 5})
       ($ :rect {:x (+ 100 x1) :y 10 :width 50 :height 50 :style {:fill (color-fn f1) :stroke-width 3 :stroke "black"}})
       ($ :rect {:x (+ 200 x2) :y 10 :width 50 :height 50 :style {:fill (color-fn f2) :stroke-width 3 :stroke "black"}})
       ($ :rect {:x (+ 300 x3) :y 10 :width 50 :height 50 :style {:fill (color-fn f3) :stroke-width 3 :stroke "black"}})
       ($ :text {:x (+ 115 x1) :y 50 :font-family "Montserrat" :font-size 48 :font-weight "Bold"} (gs/format "1"))
       ($ :text {:x (+ 215 x2) :y 50 :font-family "Montserrat" :font-size 48 :font-weight "Bold"} (gs/format "2"))
       ($ :text {:x (+ 315 x3) :y 50 :font-family "Montserrat" :font-size 48 :font-weight "Bold"} (gs/format "3"))
       ($ :text {:x 102 :y 80 :font-family "Montserrat" :font-size 18} (gs/format "s1 %.1f" x1))
       ($ :text {:x 202 :y 80 :font-family "Montserrat" :font-size 18} (gs/format "s2 %.1f" x2))
       ($ :text {:x 302 :y 80 :font-family "Montserrat" :font-size 18} (gs/format "s3 %.1f" x3))
       ($ :text {:x 102 :y 100 :font-family "Montserrat" :font-size 18} (gs/format "f1 %.1f" f1))
       ($ :text {:x 202 :y 100 :font-family "Montserrat" :font-size 18} (gs/format "f2 %.1f" f2))
       ($ :text {:x 302 :y 100 :font-family "Montserrat" :font-size 18} (gs/format "f3 %.1f" f3))
       )))

(defui reset-displacements-button []
  ($ :div ($ :button
             {:on-click #(rf/dispatch [:displacement/reset-app-db])}
             "Reset")))


(defui header []
  ($ :header.app-header
     ($ :div {:width 32} 
        ($ :p {:style {:font-family "Montserrat" :font-size 48}} "Network of Springs"))))

(defui footer []
  ($ :footer.app-footer
     ($ :small "made by Daniel Craig")))

(defui displacement-field [{:keys [on-edit-displacement i]}]
  (let [displacement (hooks/use-subscribe [:app/initial-displacement i])]
    ($ :div
       ($ :div 
          ($ :p {:style {:font-family "Montserrat" :font-size 14}} 
             "Initial Displacement " (inc i)))
       ($ :input
          {:value displacement
           :type :range
           :min -15
           :max 15
           :placeholder 0
           :style {:width "80%"}
           :on-change (fn [^js e]
                        (on-edit-displacement (int (.. e -target -value))))}))))

(defui time-field [{:keys [on-edit-time]}]
  (let [time (hooks/use-subscribe [:app/time])]
    ($ :div
       ($ :div
          ($ :p {:style {:font-family "Montserrat" :font-size 14}} "Time step"))
       ($ :input
          {:value time
           :type :range
           :min 0
           :max 50
           :placeholder 0
           :style {:width "80%"}
           :on-change (fn [^js e]
                        (on-edit-time (int (.. e -target -value))))}))))


(defui app []
  (let [todos (hooks/use-subscribe [:app/todos])]
    ($ :.app
       ($ header)
       ($ spring-network-view)
       ($ reset-displacements-button)
       ($ displacement-field {:i 0 :on-edit-displacement #(rf/dispatch [:displacement/update-displacement 0 %])})
       ($ displacement-field {:i 1 :on-edit-displacement #(rf/dispatch [:displacement/update-displacement 1 %])})
       ($ displacement-field {:i 2 :on-edit-displacement #(rf/dispatch [:displacement/update-displacement 2 %])})
       ($ time-field         {:on-edit-time #(rf/dispatch [:time/update-time %])})
       ($ footer))))

(defonce root
  (uix.dom/create-root (js/document.getElementById "root")))

(defn render []
  (rf/dispatch-sync [:app/init-db app.db/default-db])
  (uix.dom/render-root ($ app) root))

(defn ^:export init []
  (render))

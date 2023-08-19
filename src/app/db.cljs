(ns app.db
  (:require 
   [emmy.matrix :as matrix]))

(def spring-matrix
  [[1 -1 0]
   [-1 2 -1]
   [0 -1 1]])

(def displacement-vector
  [[0 0 1]])

(def default-db
  {:todos (sorted-map-by >)
   :app-state {:springs spring-matrix
               :displacement displacement-vector}})

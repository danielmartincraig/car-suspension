(ns app.db
  (:require 
   [emmy.matrix :as matrix]))

(def spring-matrix
  [[1 -0.25 0]
   [-0.25 2 -0.25]
   [0 -0.25 1]])

(def displacement-vector
  [[0]
   [0]
   [0]])

(def initial-momentum-vector
  [[0]
   [0]
   [0]])

(def default-db
  {:app-state {:springs spring-matrix
               :displacement displacement-vector
               :initial-momentum initial-momentum-vector
               :time 0}})

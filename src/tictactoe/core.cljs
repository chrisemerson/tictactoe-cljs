(ns tictactoe.core
  (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

(def initial-state {:board [["-" "-" "-"] ["-" "-" "-"] ["-" "-" "-"]] :nextplayer "X"})

(def winning-states [[[0 0] [1 1] [2 2]] [[0 2] [1 1] [2 0]] [[0 0] [0 1] [0 2]] [[1 0] [1 1] [1 2]]
                     [[2 0] [2 1] [2 2]] [[0 0] [1 0] [2 0]] [[0 1] [1 1] [2 1]] [[0 2] [1 2] [2 2]]])

(defonce app-state (atom initial-state))

(defn reset-board
  []
  (reset! app-state initial-state))

(defn state-winner
  [state]
  (if (apply = (map #(get-in (:board @app-state) %) state))
    (let
      [winner (get-in (:board @app-state) (first state))]
      (if (= "-" winner) false winner))
    false))

(defn winner?
  []
  (let
    [won-states (filter #(not= false %) (map state-winner winning-states))]
    (if (= 0 (count won-states)) false (first won-states))))

(defn make-move
  [irow icell]
  (if (and (not (winner?)) (= "-" (get-in (:board @app-state) [irow icell])))
    (do
      (swap! app-state assoc-in [:board irow icell] (:nextplayer @app-state))
      (swap! app-state update-in [:nextplayer] #(if (= % "X") "O" "X")))))

(defn board
  []
  (for [irow (range 0 (count (:board @app-state)))]
    [:div.row
      (for [icell (range 0 (count (get (:board @app-state) irow)))]
        (let
          [cell (get-in (:board @app-state) [irow icell])]
          [:div.cell {:on-click #(make-move irow icell)} (if (= cell "-") "" cell)]))]))

(defn tic-tac-toe
  []
  [:div
    [:h1 "Tic Tac Toe"]
    [:div.board (board)]
    [:input {:type "button" :value "Reset" :on-click reset-board}]
    (if (winner?) [:div.winner (str (winner?) " wins!")])])

(reagent/render-component [tic-tac-toe] (. js/document (getElementById "app")))

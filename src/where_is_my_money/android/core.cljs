(ns where-is-my-money.android.core
  (:require [reagent.core :as r :refer [atom]]
            ["@react-navigation/native" :refer [NavigationContainer]]
            ["@react-navigation/stack" :refer [createStackNavigator]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [where-is-my-money.events]
            [where-is-my-money.subs]))

(def ReactNative (js/require "react-native"))

(def app-registry (.-AppRegistry ReactNative))
(def text (r/adapt-react-class (.-Text ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))
(def image (r/adapt-react-class (.-Image ReactNative)))
(def touchable-highlight (r/adapt-react-class (.-TouchableHighlight ReactNative)))

(def navigation-container (r/adapt-react-class NavigationContainer))
(def stack (createStackNavigator))
(def navigator (r/adapt-react-class (.-Navigator stack)))
(def screen (r/adapt-react-class (.-Screen stack)))

(def logo-img (js/require "./images/cljs.png"))

(defn alert [title]
      (.alert (.-Alert ReactNative) title))

(defn home-screen []
  (let [greeting (subscribe [:get-greeting])]
    (fn []
      [view {:style {:flex-direction "column" :margin 40 :align-items "center"}}
       [text {:style {:font-size 30 :font-weight "100" :margin-bottom 20 :text-align "center"}} @greeting]
       [image {:source logo-img
               :style  {:width 80 :height 80 :margin-bottom 30}}]
       [touchable-highlight {:style {:background-color "#999" :padding 10 :border-radius 5}
                             :on-press #(alert "HELLO!")}
        [text {:style {:color "white" :text-align "center" :font-weight "bold"}} "press me"]]])))

(defn app-root []
  (fn []
    [navigation-container
     [navigator
      [screen {:name "Home" :component (r/reactify-component home-screen)}]]]))

(defn init []
      (dispatch-sync [:initialize-db])
      (.registerComponent app-registry "WhereIsMyMoney" #(r/reactify-component app-root)))

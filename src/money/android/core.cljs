(ns money.android.core
  (:require [reagent.core :as r :refer [atom]]
            ["@react-navigation/native" :refer [NavigationContainer]]
            ["@react-navigation/stack" :refer [createStackNavigator]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [money.events]
            [money.subs]))

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

(defn account [acc-name]
  [text {:style {:font-size 30 :font-weight "100" :margin-bottom 20 :text-align "center"}} acc-name])

(defn home-screen []
  (let [account-names (subscribe [:account-names])]
    (fn [{:keys [navigation]}]
      [view {:style {:flex-direction "column" :margin 40 :align-items "center"}}
       (for [[idx acc-name] (map-indexed vector (:account-names @account-names))]
         ^{:key idx} [account acc-name])
       [image {:source logo-img
               :style  {:width 80 :height 80 :margin-bottom 30}}]
       [touchable-highlight {:style {:background-color "#999" :padding 10 :border-radius 5 :margin-bottom 30}
                             :on-press #(alert "HELLO!")}
        [text {:style {:color "white" :text-align "center" :font-weight "bold"}} "press me"]]
       [touchable-highlight {:style {:background-color "#999" :padding 10 :border-radius 5}
                             :on-press #(.navigate navigation "About")}
        [text {:style {:color "white" :text-align "center" :font-weight "bold"}} "About"]]])))

(defn about-screen []
  [view {:style {:align-items "center"}}
   [text {:style {:margin-top 30}}
    "This cool app was developed by Michael"]])

(defn app-root []
  (fn []
    [navigation-container
     [navigator {:initialRouteName "Home"}
      [screen {:name "Home" :component (r/reactify-component home-screen)}]
      [screen {:name "About" :component (r/reactify-component about-screen)}]]]))

(defn init []
      (dispatch-sync [:initialize-db {}])
      (.registerComponent app-registry "WhereIsMyMoney" #(r/reactify-component app-root)))

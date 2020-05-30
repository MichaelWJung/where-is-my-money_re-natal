(ns money.android.core
  (:require [reagent.core :as r :refer [atom]]
            ["@react-navigation/native" :refer [NavigationContainer]]
            ["@react-navigation/stack" :refer [createStackNavigator]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [money.presenters.transaction-presenter :as tp]
            [money.events]
            [money.subs]))

(def ReactNative (js/require "react-native"))

(def app-registry (.-AppRegistry ReactNative))
(def text (r/adapt-react-class (.-Text ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))
(def picker (r/adapt-react-class (.-Picker ReactNative)))
(def picker-item (r/adapt-react-class (.. ReactNative -Picker -Item)))
(def image (r/adapt-react-class (.-Image ReactNative)))
(def touchable-highlight (r/adapt-react-class (.-TouchableHighlight ReactNative)))
(def touchable-native-feedback (r/adapt-react-class (.-TouchableNativeFeedback ReactNative)))

(def navigation-container (r/adapt-react-class NavigationContainer))
(def stack (createStackNavigator))
(def navigator (r/adapt-react-class (.-Navigator stack)))
(def screen (r/adapt-react-class (.-Screen stack)))

(defn alert [title]
      (.alert (.-Alert ReactNative) title))

(defn account-picker []
  (let [accounts (subscribe [:account-names])]
    (fn []
      [view
       [picker {:selectedValue (:account-idx @accounts) :style {:height 50 :width 150}
                :onValueChange #(dispatch [:set-account %])}
        (for [[idx acc-name] (map-indexed vector (:account-names @accounts))]
          ^{:key idx} [picker-item {:label acc-name :value idx}])]])))

(defn transaction [{:keys [data navigation]}]
  [touchable-native-feedback
   {:on-press (fn []
                (dispatch [:edit-transaction (:id data)])
                (.navigate navigation "Transaction"))}
   [view
    [text (str (:description data) " " (:amount data))]
    [text (str (:date data) " " (:account data)
               " " (:balance data))]]])

(defn account-overview []
  (let [overview (subscribe [:account-overview])]
    (fn [{:keys [navigation]}]
      [view
       (for [transaction-data @overview]
         ^{:key (:id transaction-data)}
         [transaction {:navigation navigation :data transaction-data}])])))

(defn transaction-screen []
  (let [data (subscribe [:transaction-screen])]
    (fn []
      (let [d @data]
        [view
         [text "Description: " (::tp/description d)]
         [text "Date: " (::tp/date d)]]))))

(defn home-screen []
  (let [account-names (subscribe [:account-names])]
    (fn [{:keys [navigation]}]
      [view {:style {:flex-direction "column" :margin 40 :align-items "center"}}
       [account-picker]
       [account-overview {:navigation navigation}]
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
      [screen {:name "About" :component (r/reactify-component about-screen)}]
      [screen {:name "Transaction" :component (r/reactify-component transaction-screen)}]]]))

(defn init []
      (dispatch-sync [:initialize-db {}])
      (.registerComponent app-registry "WhereIsMyMoney" #(r/reactify-component app-root)))

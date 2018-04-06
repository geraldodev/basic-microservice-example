(ns the-next-big-server-side-thing.components.system-utils
  (:import [clojure.lang ExceptionInfo])
  (:require [com.stuartsierra.component :as component]
            [schema.core :as s]))

(def system (atom nil))

(defn- quiet-start [system]
  (try
    (component/start system)
    (catch ExceptionInfo ex
      (throw (or (.getCause ex) ex)))))

(s/defn start-system! []
  (swap! system quiet-start))

(s/defn get-component [component-name]
  (some-> system deref (get component-name)))

(s/defn get-component! [c]
  (or (get-component c)
      (throw (ex-info "Component not found"
                      {:from      ::get-component!
                       :component c
                       :reason    "Unknown component"}))))

(defn stop-components! []
  (swap! system #(component/stop %)))

(defn clear-components! []
  (reset! system nil))

(defn stop-system! []
  (stop-components!)
  (shutdown-agents))

(s/defn ^:private system-for-env [environment systems]
  (get systems environment (:base-system systems)))

(s/defn bootstrap! [systems-map environment]
  (let [system-map ((system-for-env environment systems-map))]
    (->> system-map
         component/start
         (reset! system))))

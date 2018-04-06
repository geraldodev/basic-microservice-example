(ns the-next-big-server-side-thing.aux.init
  (:require [the-next-big-server-side-thing.components :as components]
            [the-next-big-server-side-thing.components.mock-http :as mh-com]))

(defn init! [world]
  (let [system (components/ensure-system-up! :test-system)]
    (println "init started system\t" system)
    (mh-com/clear-requests! (:http system))
    (-> world
        (assoc :system system))))

(defn restart! [world]
  (println "stopped system\t" (components/stop-system!))
  (let [system (components/ensure-system-up! :test-system)]
    (println "started system\t" system)
    (-> world
        (assoc :system system))))

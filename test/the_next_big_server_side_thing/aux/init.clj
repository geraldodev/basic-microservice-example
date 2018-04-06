(ns the-next-big-server-side-thing.aux.init
  (:require [the-next-big-server-side-thing.components :as components]
            [the-next-big-server-side-thing.components.system-utils :as system-utils]
            [the-next-big-server-side-thing.components.mock-http :as mh-com]))

(defn init! [world]
  (let [system (components/ensure-system-up! :test-system)]
    (mh-com/clear-requests! (:http system))
    (-> world
        (assoc :system system))))

(defn restart! [world]
  (system-utils/clear-components!)
  (let [system (components/ensure-system-up! :test-system)]
    (-> world
        (assoc :system system))))

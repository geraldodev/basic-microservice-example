(ns the-next-big-server-side-thing.server
  (:gen-class)
  (:require [io.pedestal.http :as server]
            [io.pedestal.http.route :as route]
            [the-next-big-server-side-thing.components :as components]
            [the-next-big-server-side-thing.service :as service]))

(defn run-dev
  "The entry-point for 'lein run-dev'"
  [& args]
  (println "\nCreating your [DEV] server...")
  (components/create-and-start-system! :local-system))

(defn -main
  "The entry-point for 'lein run'"
  [& args]
  (println "\nCreating your server...")
  (components/create-and-start-system! :base-system))

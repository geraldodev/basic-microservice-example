(ns the-next-big-server-side-thing.interceptors.error-info
  (:require [the-next-big-server-side-thing.protocols.debug-logger :as protocols.debug-logger]
            [io.pedestal.interceptor.error :as interceptor.error]))

(defn- register-error-for-debugging [context error]
  (some-> context :request :components :debug-logger
          (protocols.debug-logger/register-last-error! error)))

(def log-error-during-debugging
 ; {:name ::catch :error })
  (interceptor.error/error-dispatch [ctx ex]
    :else
    (do
    (register-error-for-debugging ctx ex)
    (assoc ctx :io.pedestal.interceptor.chain/error ex))))


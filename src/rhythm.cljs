(ns rhythm)

(defn- create-event
  [action]
  {:action action})

(defn- create-state
  [in out]
  {:in in
   :out out})

(defn actor
  "Create actor"
  [name & states]
  (atom {:name name
         :current (set states)
         :watchers {}
         :states {}
         :events {}}))

(defn- can-assoc?
  [s key]
  (not (contains? (:current s) key)))

(defn- can-dissoc?
  [s key]
  (contains? (:current s) key))

(defn is-state?
  [s key]
  (contains? (:current @s) key))

(defn- assoc-state
  [s key]
  (assoc s :current
         (conj (:current s) key)))

(defn- dissoc-state
  [s key]
  (assoc s :current
         (disj (:current s) key)))

(defn- assoc-watcher
  [s key fn]
  (assoc s :watchers
         (merge-with concat (:watchers s) {key [fn]})))

(defn add-state!
  ""
  [s key in out]
  (swap! s assoc-in [:states key] (create-state in out)))

(defn add-event!
  ""
  [s key action]
  (swap! s assoc-in [:events key] (create-event action)))

(defn watch!
  ""
  [s key fn]
  (swap! s assoc-watcher key fn))

(defn- get-state
  [s key]
  (get (:states @s) key))

(defn- get-event
  [s key]
  (get (:events @s) key))

(defn- get-watchers
  [s key]
  (get (:watchers @s) key))

(defn on!
  ""
  [s key & args]
  (when (can-assoc? @s key)
    (let [e (get-state s key)
          in (:in e)
          watchers (get-watchers s key)]
      (swap! s assoc-state key)
      (when-not (nil? in)
        (apply in args))
      (when-not (nil? watchers)
        (doseq [w watchers]
          (apply w args))))))

(defn safe-on!
  ""
  [s pre-key key & args]
  (if (is-state? s pre-key)
    (apply on! (concat [s key] args))
    (js/setTimeout
     #(apply safe-on! (concat [s pre-key key] args))
     500)))

(defn off!
  ""
  [s key & args]
  (when (can-dissoc? @s key)
    (let [e (get-state s key)
          out (:out e)]
      (swap! s dissoc-state key)
      (when-not (nil? out)
        (apply out args)))))


(defn safe-off!
  ""
  [s pre-key key & args]
  (if (is-state? s pre-key)
    (apply off! (concat [s key] args))
    (js/setTimeout
     #(apply safe-off! (concat [s pre-key key] args))
     500)))

(defn switch-state!
  ""
  [s pre-key key & args]
  (apply off! (concat [s pre-key] args))
  (apply on! (concat [s key] args)))

(defn trigger-event!
  ""
  [s key & args]
  (let [e (get-event s key)
        action (:action e)
        watchers (get-watchers s key)]
    (when-not (nil? action)
      (apply action args))
    (when-not (nil? watchers)
        (doseq [w watchers]
          (apply w args)))))

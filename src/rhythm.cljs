(ns rhythm)

(defn- create-action
  [action]
  {:action action})

(defn- create-state
  [in out]
  {:in in
   :out out})

(defn actor
  "Create actor"
  [name {:keys [states data]
         :or {states []
              data {}}}]
  (atom {:name name
         :current (set states)
         :watchers {}
         :states {}
         :actions {}
         :data data}))

(defn actor-name
  ""
  [a]
  (:name @a))

(defn actor-data
  ""
  [a]
  (:data @a))

(defn- can-assoc?
  [a key]
  ((complement contains?) (:current a) key))

(defn- can-dissoc?
  [a key]
  (contains? (:current a) key))

(defn is-state?
  ""
  [a key]
  (contains? (:current @a) key))

(defn- assoc-state
  [a key]
  (assoc a :current
         (conj (:current a) key)))

(defn- dissoc-state
  [a key]
  (assoc a :current
         (disj (:current a) key)))

(defn- assoc-watcher
  [a key fn]
  (assoc a :watchers
         (merge-with concat (:watchers a) {key [fn]})))

(defn add-state!
  ""
  [a key {:keys [in out]
          :or {in #()
               out #()}}]
  (swap! a assoc-in [:states key] (create-state in out))
  a)

(defn add-action!
  ""
  [a key action]
  (swap! a assoc-in [:actions key] (create-action action))
  a)

(defn watch-state-changes!
  ""
  [a key fn]
  (swap! a assoc-watcher key fn))

(defn- get-state
  [a key]
  (get (:states @a) key))

(defn- get-action
  [a key]
  (get (:actions @a) key))

(defn- get-watchers
  [a key]
  (get (:watchers @a) key))

(defn on!
  ""
  [a key & args]
  (let [e (get-state a key)
        in (:in e)
        watchers (get-watchers a key)]
    (swap! a assoc-state key)
    (when-not (nil? in)
      (apply in args))
    (when-not (nil? watchers)
      (doseq [w watchers]
        (apply w args)))))

(defn off!
  ""
  [a key & args]
  (let [e (get-state a key)
        out (:out e)]
    (swap! a dissoc-state key)
    (when-not (nil? out)
      (apply out args))))

(defn switch-state!
  ""
  [a pre-key key & args]
  (when-not (nil? pre-key)
    (apply off! (concat [a pre-key] args)))
  (apply on! (concat [a key] args)))

(defn trigger-action!
  ""
  [a key & args]
  (let [e (get-action a key)
        action (:action e)]
    (if-not (nil? action)
      (apply action args)
      (throw (js/Error. (str "Action is not defined: " (pr-str key)))))))

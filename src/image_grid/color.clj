(ns image-grid.color)

(defn hsv->rgb
  "Given a color in the HSV color system, convert to the equivalent RGB.
   h, s, and v are all assumed to be in [0, 1].
   Return: [r, g, b], with all in [0, 1].
   Uses this formula: https://en.wikipedia.org/wiki/HSL_and_HSV#HSV_to_RGB"
  [[h s v]]
  (let [chroma (* v s)
        h-prime (* h 6.0)
        x (* chroma (- 1 (abs (- (mod h-prime 2) 1))))
        rgb1 (cond
               (< h-prime 1) [chroma x 0]
               (< h-prime 2) [x chroma 0]
               (< h-prime 3) [0 chroma x]
               (< h-prime 4) [0 x chroma]
               (< h-prime 5) [x 0 chroma]
               :else         [chroma 0 x])
        m (- v chroma)]
    (mapv #(+ % m) rgb1)))

(defn rgb->hsv
  "Given a color in the RGB color system, convert to the equivalent HSV.
   r, g, and b are all assumed to be in [0, 1]
   Return: [h, s, v], with all in [0, 1].
   Uses this formula: https://en.wikipedia.org/wiki/HSL_and_HSV#From_RGB"
  [[r g b]]
  (let [v (max r g b)
        xmin (min r g b)
        c (- v xmin)
        h (cond
            (zero? c) 0.0
            (= v r) (/ (mod (/ (- g b)
                               c)
                            6)
                       6.0)
            (= v g) (/ (+ 2 (/ (- b r)
                               c))
                       6.0)
            :else (/ (+ 4 (/ (- r g)
                             c))
                     6.0))
        s (if (zero? v)
            0.0
            (/ c v))]
    [h s v]))


;; Examples:
(comment

  (hsv->rgb [0.55 0.8 0.7])
  ;; => [0.14 0.5319999999999998 0.7]

  (rgb->hsv (hsv->rgb [0.55 0.8 0.7]))
  ;; => [0.55 0.7999999999999999 0.7]

  (rgb->hsv [0.2 0.2 0.2])
  ;; => [0.0 0.0 0.2]

  (rgb->hsv [0.4 0.4 0.1])
  ;; => [0.16666666666666666 0.7500000000000001 0.4]
  
  )
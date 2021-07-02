(ns fortunedb)

(def db
  ["The early bird gets the worm, but the second mouse gets the cheese."
   "Be on the alert to recognize your prime at whatever time of your life it may occur."
   "Your road to glory will be rocky, but fulfilling."
   "Courage is not simply one of the virtues, but the form of every virtue at the testing point."
   "Patience is your alley at the moment. Don't worry!"
   "Nothing is impossible to a willing heart."
   "Don’t worry about money. The best things in life are free."
   "Don’t pursue happiness – create it."
   "Courage is not the absence of fear; it is the conquest of it."
   "Nothing is so much to be feared as fear."
   "All things are difficult before they are easy."
   "The real kindness comes from within you."
   "A ship in harbor is safe, but that’s not why ships are built."
   "You don’t need strength to let go of something. What you really need is understanding."
   "If you want the rainbow, you have to tolerate the rain."
   "Fear is interest paid on a debt you may not owe."
   "Hardly anyone knows how much is gained by ignoring the future."
   "The wise man is the one that makes you think that he is dumb."
   "The usefulness of a cup is in its emptiness."
   "He who throws mud loses ground."
   "Success lies in the hands of those who wants it."
   "To avoid criticism, do nothing, say nothing, be nothing."
   "One that would have the fruit must climb the tree."
   "It takes less time to do a thing right than it does to explain why you did it wrong."
   "Big journeys begin with a single step."
   "Of all our human resources, the most precious is the desire to improve."
   "Do the thing you fear and the death of fear is certain."
   "You never show your vulnerability, you are always self assured and confident."
   "People learn little from success, but much from failure."
   "Be not afraid of growing slowly, be afraid only of standing still."
   "We must always have old memories and young hopes."
   "A person who won’t read has no advantage over a person who can’t read."
   "He who expects no gratitude shall never be disappointed."
   "I hear and I forget. I see and I remember. I do and I understand."
   "The best way to get rid of an enemy is to make a friend."
   "It’s amazing how much good you can do if you don’t care who gets the credit."
   "Never forget that a half truth is a whole lie."
   "Happiness isn’t an outside job, it’s an inside job."
   "If you do no run your subconscious mind yourself, someone else will."
   "Yes by calling full, you created emptiness."])

(defn get-cookie
  ([index]
   (nth db index))
  ([]
   (let [size (count db)
         index (rand-int size)]
     (get-cookie index))))

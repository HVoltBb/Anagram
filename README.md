# Anagram 0.2
This version utilizes a set of preprocessed key and value pairs of words with length from 4 to 8. This range is used to cover the range of potential anagrams needed in the app. The key and value pairs are stored seperately in the assets folder. The app load these files on start. The preprocessing step is accomplished using the PreProcess program. 

In this version of the app, most of the computation in constructing the anagram has been relegated. Now, the most computational intensive step is the one to look up the hashmap with 26 candidate words, and it will not significantly slow down the app because these operations are very efficient.

# Anagram 0.1
This my implementation of the first android app of the Google Applied CS with Android course.
It has support of multi-thread processing for multi-core processors.
This app uses Maps to store and retrieve anagrams.

This app has the following logic:

1, Most actions take place in this step. The user clicks the button to initialize the game. A pool of threads is initialized to run tasks. The constructor of the AnagramDictionary read in the word list into three containers for different purposes in the program: a set (S) for verifying word membership of a user provided word with the dictionary, a map (M1) of lists keyed using word length for efficient retrieval of potential words to check in the getAnagram function, and a map (M2) of lists also keyed using word length for the pool of words to start with. Then, a word (W) with the DEFAULT_WORD_LENGTH is chosen to start with. This word plus one additional letter must have at least 5 anagrams. The initial word is chosen iteratively until the condition is met, and the word that doesn't meet this condition is removed from the pool (M2). The list of anagrams for each candidate word (W plus one additional letter from the English alphabet) were recorded in the map (M1), and the result for all the candidate words is stored in a list. Additionally, the program checks if each anagram in the list is legal.

2, In the second step, user input words into the text box, and the program checks if it is a legal answer and if there is a match with the cached result. A set would be used here for better performance. However, the number of members in the result would not exceed 20, and I don't expect any discernible performance boost by switching to a set.

3, In the second step, user clicks the button to reveal any additional words that have been missed.

4, The user can click on the button again to continue playing with a new word. The new word would be 1 character longer if the previous word length is smaller than the maximum length, otherwise the maximum length. Again, the program search iteratively through the pool, and remove any unfit words. However, this removal of words will not improve app performance unless the user play through all the words at the maximum level, and the search loop over to the start of the list of the pool. 

The most time-consuming step is the one to pick a candidate word. The program need to search 26 possible words for anagrams. Single-thread execution of this step took 5-10s on my phone (Samsung note 3 running lollipop). Multi-threaded execution of this step took just a little over 1s (task scheduler set to high performance). The scheduler setup on my phone also impacts performance of multi-threaded tasks.

The performance of the app should improve as the user play through more and more words. Alternatively, I could pre-process the word list, and construct the full anagram map (M1), and the candidate pool (M2). The app would just load these two objects on start to avoid most computation.

This is my first app. I hope this post can help others on this path.

5/21/2018

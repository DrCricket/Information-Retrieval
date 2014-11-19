Information-Retrieval
=====================

Fuzzy Logic Controllers (FLCs) are based on expert systems, which employ fuzzy logic.
FLCs make use of rules, rather than equations to make computations.
For the controller designed in this project, we have written 4 rules of the IF-THEN format, popularly referred to as the Mamdani type rules.
It is a direct controller, wherein the FLC is in the forward path in a feedback control system.

Engine.
For the synonyms and thesaurus part, the following APIs were evaluated and after weighing the pros and cons of each of them, Swoogle was used.
1. <b>WordNet</b> – Works offline but thesaurus extraction is very complicated
2. BigHugeLabs – Works online only , absence of quantitative similarity between words
3. Swoogle – Works online, precise similarity measure between words

HashMap was used to index the terms of the documents and calculate the TF-IDF and BM-25 measures, while Swing was used to construct the GUI.
The only performance bottleneck is the internet connectivity and server overload in case the number of words per document is huge, which can be rectified by having an offline API as a backup.

</b>Advantages of using a FLC</b>
1. Without actually developing a model for the problem, a method for solving the problem can be evolved, which is usually difficult for ranking systems
2. The controller is inherently robust as it is capable of handling noise and changing size of corpus
3. Because of the rule-based nature of the FLC, any number of inputs can be handled to give outputs – big data may not be a problem as long as we write rules for the rule base.
The four rules of the rule base are –
1. If value is low the relevance is zero
2. If value is medium the relevance is low
3. If value is high the relevance in high
4. If value is very high the relevance is very high
where value = 0.5*Similarity + 0.25*TF-IDF + 0.25*BM25 and similarity is a novel concept introduced, wherein Similarity is the measure of “how similar the query is to a particular document”. The API Swoogle is used to determine the similarity between the query term and each of the documents indexed terms and finally they are added and normalized to get the Similarity.
The value is the weighted mean of TF-IDF, BM25 and Similarity, which is then fed as the input to the controller.
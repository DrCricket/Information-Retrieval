Information-Retrieval
=====================

Fuzzy Logic Controllers (FLCs) are based on expert systems, which employ fuzzy logic.
FLCs make use of rules, rather than equations to make computations.
For the controller designed in this project, we have written 4 rules of the IF-THEN format, popularly referred to as the Mamdani type rules.
It is a direct controller, wherein the FLC is in the forward path in a feedback control system.

For the synonyms and thesaurus part, the following APIs were evaluated and after weighing the pros and cons of each of them, Swoogle was used.<br>
1. <b>WordNet</b> – Works offline but thesaurus extraction is very complicated<br>
2. <b>BigHugeLabs</b> – Works online only , absence of quantitative similarity between words<br>
3. <b>Swoogle</b> – Works online, precise similarity measure between words<br>

HashMap was used to index the terms of the documents and calculate the TF-IDF and BM-25 measures, while Swing was used to construct the GUI.
The only performance bottleneck is the internet connectivity and server overload in case the number of words per document is huge, which can be rectified by having an offline API as a backup.

<b>Advantages of using an FLC </b>
<ol>
<li> Without actually developing a model for the problem, a method for solving the problem can be evolved, which is usually difficult for ranking systems</li>
<li> The controller is inherently robust as it is capable of handling noise and changing size of corpus</li>
<li> Because of the rule-based nature of the FLC, any number of inputs can be handled to give outputs – big data may not be a problem as long as we write rules for the rule base.</li>
</ol>
The four rules of the rule base are –
<ul>
<li> If value is low the relevance is zero </li>
<li>If value is medium the relevance is low</li>
<li>If value is high the relevance in high</li>
<li>If value is very high the relevance is very high</li>
</ul>

where <i>value = 0.5*Similarity + 0.25*TF-IDF + 0.25*BM25</i> and similarity is a novel concept introduced, wherein Similarity is the measure of “how similar the query is to a particular document”. The API Swoogle is used to determine the similarity between the query term and each of the documents indexed terms and finally they are added and normalized to get the Similarity.
The value is the weighted mean of TF-IDF, BM25 and Similarity, which is then fed as the input to the controller.
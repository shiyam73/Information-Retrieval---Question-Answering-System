Information-Retrieval---Question-Answering-System
=================================================

The QA system developed aims to robustly answer the user's queries. Wikipedia documents are indexed
using SolrJ. The query that the user enters into the system is then processed using NLP and key words
are extracted. These key words are then sent to the index and queried. The possible answers are then
retrieved and ranked using Solr's inbuilt ranking features and the top results are displayed.

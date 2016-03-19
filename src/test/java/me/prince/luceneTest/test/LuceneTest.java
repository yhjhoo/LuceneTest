package me.prince.luceneTest.test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.*;
import org.apache.lucene.util.QueryBuilder;
import org.junit.After;
import org.junit.Test;

public class LuceneTest {

	@Test
	public void testIndex() throws IOException {

		// File index
		Directory directory = FSDirectory.open(new File("Index").toPath());

		// memory index
		Directory ramDirectory = new RAMDirectory();
		
		
		Analyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		IndexWriter indexWriter = new IndexWriter(directory, config);
		
		File folder = new File("documents");
		
		indexWriter.deleteAll();
		File[] files = folder.listFiles();
		int i=0;
		for(File file : files){
			i++;
			System.out.println("Indexing file " + file.getCanonicalPath());
            Document doc = new Document();
            doc.add(new TextField("content", new FileReader(file)));
            //doc.add(new StoredField("fileName", file.getName() ));
            doc.add(new TextField("fileName", file.getName(), Store.YES ));
            
            doc.add(new TextField("seq", "1" , Store.YES ));
            indexWriter.addDocument(doc);
            
            //indexWriter.deleteDocuments(new Term("fileName", file.getCanonicalPath()));
//            indexWriter.deleteAll();
            //indexWriter.updateDocument(new Term("fileName", file.getCanonicalPath()), doc);
		}
		
		System.out.println("doc indexed: " + indexWriter.maxDoc() );
		
		indexWriter.close();
	}
	
	
	@Test
	public void testSearchTerm() throws IOException{
		String q = "1";

		Directory directory = FSDirectory.open(new File("Index").toPath());
	
		IndexReader  indexReader  = DirectoryReader.open(directory);
	
		IndexSearcher searcher = new IndexSearcher(indexReader);
	
//		Query termQuery = new TermQuery(new Term("seq","1"));
//		Query termQuery = new TermQuery(new Term("fileName","sports.eml"));
//		Query termQuery = new WildcardQuery(new Term("fileName","*sports.eml*"));
		
//		Query termQuery = new TermQuery(new Term("seq","1"));
		Query termQuery = new TermQuery(new Term("fileName","readme.txt"));
//		Query termQuery = new WildcardQuery(new Term("fileName","*readme.txt*"));
	
		TopDocs topDocs =searcher.search(termQuery, 1000);
	
		ScoreDoc[] hits = topDocs.scoreDocs;
	
		for (ScoreDoc hit : hits) {
		        int docId = hit.doc;
		        Document d = searcher.doc(docId);
		        System.out.println(d.get("fileName") + " Score :" + hit.score);
		}
	
		System.out.println("Found " + hits.length);
	}
	@Test
	public void testDelete() throws IOException{
		Directory directory = FSDirectory.open(new File("Index").toPath());
		IndexReader indexReader = DirectoryReader.open(directory);
		IndexSearcher searcher = new IndexSearcher(indexReader);
		
		
		Analyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		IndexWriter indexWriter = new IndexWriter(directory, config);
//		indexWriter.maxDoc();
		
		
		Query termQuery = new TermQuery(new Term("fileName","README"));
		
		TopDocs topDocs = searcher.search(termQuery, 100);
		
		ScoreDoc[] hits = topDocs.scoreDocs;
		System.out.println("hit: " + hits.length);
		for (int i = 0; i < hits.length; i++) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			System.out.println(d.get("fileName") + " Score :" + hits[i].score);
		}

//		indexWriter.deleteDocuments(new Term("fileName", "sports.eml"));
		indexWriter.deleteDocuments(termQuery);
		System.out.println("doc indexed: " + indexWriter.numDocs() );
		indexWriter.close();
	}

	@Test
	public void search() throws IOException {
		Directory directory = FSDirectory.open(new File("Index").toPath());
		IndexReader indexReader = DirectoryReader.open(directory);
		IndexSearcher searcher = new IndexSearcher(indexReader);

		Analyzer analyzer = new StandardAnalyzer();
		QueryBuilder builder = new QueryBuilder(analyzer);

		String queryStr = "football";
		Query query = builder.createBooleanQuery("content", queryStr);
		TopDocs topDocs = searcher.search(query, 100);

		ScoreDoc[] hits = topDocs.scoreDocs;
		for (int i = 0; i < hits.length; i++) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			System.out.println(d.get("fileName") + " Score :" + hits[i].score);
			System.out.println(d.get("seq") + " Score :" + hits[i].score);
		}
		System.out.println("Found " + hits.length);

	}
}

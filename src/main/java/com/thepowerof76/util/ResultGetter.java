package com.thepowerof76.util;

import com.thepowerof76.metasearch_backend.util.MetaResult;
import com.thepowerof76.metasearch_backend.util.Result;
import com.thepowerof76.metasearch_backend.util.ResultMixin;
import com.thepowerof76.metasearch_backend.util.extractor.*;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ResultGetter {
    private final GoogleExtractor g = new GoogleExtractor();
    private final BingExtractor b = new BingExtractor();
    private final DDGExtractor d = new DDGExtractor();
    private final ArchiveExtractor a = new ArchiveExtractor();
    private final BaseExtractor bs = new BaseExtractor();
    private final WibyExtractor w = new WibyExtractor();
//    private final ExtractorThread gT = new ExtractorThread(new GoogleExtractor());
//    private final ExtractorThread bT = new ExtractorThread(new BingExtractor());
//    private final ExtractorThread dT = new ExtractorThread(new DDGExtractor());
//    private final ExtractorThread aT = new ExtractorThread(new ArchiveExtractor());
//    private final ExtractorThread bsT = new ExtractorThread(new BaseExtractor());
//    private final ExtractorThread wT = new ExtractorThread(new WibyExtractor());
    public ArrayList<MetaResult> search(String query, int page) throws InterruptedException, ExecutionException {
        List<ExtractorThread> threadExecutors = new ArrayList<>();
        threadExecutors.add(new ExtractorThread(g));
        threadExecutors.add(new ExtractorThread(b));
        threadExecutors.add(new ExtractorThread(d));
        threadExecutors.add(new ExtractorThread(a));
        threadExecutors.add(new ExtractorThread(bs));
        threadExecutors.add(new ExtractorThread(w));
        ExecutorService executor = Executors.newFixedThreadPool(10);
        for(int i = 0; i < threadExecutors.size(); i++) {
            threadExecutors.get(i).setQueryParams(query, page);
        }
        List<Future<ArrayList<Result>>> futures = executor.invokeAll(threadExecutors);


        return ResultMixin.mixResults(futures.get(0).get(), futures.get(1).get(), futures.get(2).get(), futures.get(3).get(), futures.get(4).get(), futures.get(5).get());
    }

}

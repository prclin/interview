package com.bxtdata.interview.interview;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.bxtdata.interview.interview.task1.entity.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.ToIntFunction;

@SpringBootTest
class InterviewApplicationTests {

    @Autowired
    ObjectMapper mapper;

    private List<TagTreeNode> readTagLibrary() throws IOException {

        List<TagTreeNode> tagTree = new LinkedList<>();
        String filePath = new ClassPathResource("/static/标签词库1026.xlsx").getFile().getAbsolutePath();
        EasyExcel.read(filePath, Tag.class, new PageReadListener<Tag>(dataList -> {
            for (Tag tag : dataList) {
                List<TagTreeNode> children = insertNode(tagTree, new TagTreeNode(tag.getKey1(), tag.getName())).getChildren();
                List<TagTreeNode> children1 = insertNode(children, new TagTreeNode(tag.getKey2(), tag.getName())).getChildren();
                insertNode(children1, new TagTreeNode(tag.getKey3(), tag.getName()));
            }
        })).sheet().doRead();
        return tagTree;
    }

    private TagTreeNode insertNode(List<TagTreeNode> tree, TagTreeNode node) {
        for (TagTreeNode tagTreeNode : tree) {
            if (tagTreeNode.getKey().equals(node.getKey())) {
                return tagTreeNode;
            }
        }
        tree.add(node);
        return node;
    }

    private String findTag(List<TagTreeNode> tree, String storeName) {
        List<TagTreeNode> match = new LinkedList<>(tree);
        for (int i = 0; i < 2; i++) {
            match = match.stream()
                    .filter(node -> storeName.contains(node.getKey()))//过滤
                    .flatMap(node -> node.getChildren().stream())
                    .sorted(Comparator.comparingInt((ToIntFunction<TagTreeNode>) value -> value.getName().length()).reversed())//key长度降序排序
                    .toList();
        }

        Optional<TagTreeNode> max = match.stream()
                .filter(node -> storeName.contains(node.getKey()))
                .max(Comparator.comparingInt(value -> value.getName().length()));
        return max.isPresent() ? max.get().getName() : "";
    }

    @Test
    void TestCSV() {
        try {
            //加载tag
            List<TagTreeNode> tagTree = readTagLibrary();
            //输入文件
            File inFile = new ClassPathResource("/static/sample.csv").getFile();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(inFile, StandardCharsets.UTF_8));
            org.apache.commons.csv.CSVParser csvParser = new org.apache.commons.csv.CSVParser(bufferedReader, CSVFormat.RFC4180);

            //输出文件
            File outFile = new File(ResourceUtils.getURL("classpath:static").getPath() + "/output.csv");
            if (outFile.exists()) {
                outFile.delete();
            }
            outFile.createNewFile();
            CSVFormat csvFormat = CSVFormat.RFC4180.withHeader("task_id", "storeId", "storeName", "tag");
            CSVPrinter csvPrinter = new CSVPrinter(new BufferedWriter(new FileWriter(outFile, StandardCharsets.UTF_8)), csvFormat);

            //去重集合
            HashSet<String> outSet = new HashSet<>();

            int j = 0;
            for (CSVRecord record : csvParser) {
                if (j == 0) {
                    j++;
                    continue;
                }
                Result result = mapper.readValue(record.get(4), Response.class).getResult();
                if (result == null) continue;

                List<BaseDatum> datums = result.getData() == null ? Collections.emptyList() : result.getData();

                for (int i = 0; i < datums.size(); i++) {
                    BaseDatum datum = datums.get(i);
                    if (datum instanceof Datum1 datum1) {
                        Data1 data1 = datum1.getData();
                        if (outSet.contains(data1.getStoreId())) continue;
                        outSet.add(data1.getStoreId());
                        csvPrinter.print(record.get(1));
                        csvPrinter.print(data1.getStoreId());
                        String storeName = data1.getStoreName();
                        if (storeName != null) {
                            csvPrinter.print(storeName);
                            String tag = findTag(tagTree, storeName);
                            csvPrinter.print(tag);
                        }
                        csvPrinter.println();
                    } else if (datum instanceof Datum103 datum103) {
                        Data103 data103 = datum103.getData();
                        Resources resources = data103.getResources();
                        if (outSet.contains(resources.getStoreId())) continue;
                        outSet.add(data103.getStoreId());
                        csvPrinter.print(record.get(1));
                        csvPrinter.print(resources.getStoreId());
                        String storeName = resources.getStoreName();
                        if (storeName != null) {
                            csvPrinter.print(storeName);
                            String tag = findTag(tagTree, storeName);
                            csvPrinter.print(tag);
                        }
                        csvPrinter.println();
                    }

                }
            }
            csvPrinter.flush();
            csvPrinter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

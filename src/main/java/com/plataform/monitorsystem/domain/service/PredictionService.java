package com.plataform.monitorsystem.domain.service;

import com.plataform.monitorsystem.domain.model.Log;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.expressions.Window;
import org.apache.spark.sql.expressions.WindowSpec;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.apache.spark.sql.functions.avg;
import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.row_number;

@Service
public class PredictionService {

    private final SparkSession sparkSession;
    private final LogService logService;
    private final AnomalyDetectionService anomalyDetectionService;

    public PredictionService(SparkSession sparkSession, LogService logService, AnomalyDetectionService anomalyDetectionService) {
        this.sparkSession = sparkSession;
        this.logService = logService;
        this.anomalyDetectionService = anomalyDetectionService;
    }

    public String predictAnomalyRisk() {
        List<Log> logs = logService.findAllLogs();
        if (logs.isEmpty()) {
            return "Nenhum log encontrado para predição.";
        }

        // Cria DataFrame e calcula médias gerais usando Spark
        Dataset<Row> df = sparkSession.createDataFrame(logs, Log.class);
        VectorAssembler assembler = new VectorAssembler()
                .setInputCols(new String[]{"responseTime", "cpuUsage", "memoryUsage"})
                .setOutputCol("features");
        Dataset<Row> featureDf = assembler.transform(df);

        double avgResponseTime = featureDf.agg(avg("responseTime")).first().getDouble(0);
        double avgCpuUsage = featureDf.agg(avg("cpuUsage")).first().getDouble(0);
        double avgMemoryUsage = featureDf.agg(avg("memoryUsage")).first().getDouble(0);

        // Classifica o risco de cada métrica usando thresholds
        String riskResponse = classifyRisk(avgResponseTime, anomalyDetectionService.getResponseTimeModerateThreshold(),
                anomalyDetectionService.getResponseTimeCriticalThreshold());
        String riskCpu = classifyRisk(avgCpuUsage, anomalyDetectionService.getCpuModerateThreshold(),
                anomalyDetectionService.getCpuCriticalThreshold());
        String riskMem = classifyRisk(avgMemoryUsage, anomalyDetectionService.getMemoryModerateThreshold(),
                anomalyDetectionService.getMemoryCriticalThreshold());

        // Risco geral: se qualquer métrica for ALTO, risco ALTO; se nenhuma ALTO mas alguma MODERADA, risco MODERADO; senão BAIXO.
        String overallRisk;
        if ("ALTO".equals(riskResponse) || "ALTO".equals(riskCpu) || "ALTO".equals(riskMem)) {
            overallRisk = "ALTO";
        } else if ("MODERADO".equals(riskResponse) || "MODERADO".equals(riskCpu) || "MODERADO".equals(riskMem)) {
            overallRisk = "MODERADO";
        } else {
            overallRisk = "BAIXO";
        }

        // Realiza a análise de tendência utilizando Spark
        // 1. Cria uma coluna com número da linha (ordenado por timestamp)
        WindowSpec windowSpec = Window.orderBy("timestamp");
        Dataset<Row> dfWithRowNum = df.withColumn("rowNum", row_number().over(windowSpec));

        // 2. Obtém o total de registros e calcula o ponto médio
        long totalCount = df.count();
        long mid = totalCount / 2;

        // 3. Divide o DataFrame em duas partes e calcula as médias de cada uma
        Dataset<Row> firstHalfDF = dfWithRowNum.filter(col("rowNum").leq(mid));
        Dataset<Row> secondHalfDF = dfWithRowNum.filter(col("rowNum").gt(mid));

        double firstAvgResp = firstHalfDF.agg(avg("responseTime")).first().getDouble(0);
        double secondAvgResp = secondHalfDF.agg(avg("responseTime")).first().getDouble(0);
        double firstAvgCpu = firstHalfDF.agg(avg("cpuUsage")).first().getDouble(0);
        double secondAvgCpu = secondHalfDF.agg(avg("cpuUsage")).first().getDouble(0);
        double firstAvgMem = firstHalfDF.agg(avg("memoryUsage")).first().getDouble(0);
        double secondAvgMem = secondHalfDF.agg(avg("memoryUsage")).first().getDouble(0);

        // Se a média da segunda metade for maior que a da primeira para qualquer métrica, indicamos tendência de aumento.
        boolean increasingTrend = (secondAvgResp - firstAvgResp) > 0 ||
                (secondAvgCpu - firstAvgCpu) > 0 ||
                (secondAvgMem - firstAvgMem) > 0;
        String forecast = increasingTrend ? "em alta" : "estável";

        String predictionMessage = "Risco de anomalia atual: " + overallRisk + ".\n" +
                "Média de tempo de resposta: " + avgResponseTime + " ms.\n" +
                "Média de uso de CPU: " + avgCpuUsage + "%.\n" +
                "Média de uso de memória: " + avgMemoryUsage + " MB.\n" +
                "Tendência de risco futuro: " + forecast + ".";

        return predictionMessage;
    }

    private String classifyRisk(double value, double moderateThreshold, double criticalThreshold) {
        if (value > criticalThreshold) {
            return "ALTO";
        } else if (value > moderateThreshold) {
            return "MODERADO";
        } else {
            return "BAIXO";
        }
    }
}

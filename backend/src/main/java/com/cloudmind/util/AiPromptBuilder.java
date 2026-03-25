package com.cloudmind.util;

/**
 * Prompt 封装工具
 */
public class AiPromptBuilder {

    private AiPromptBuilder() {
    }

    /**
     * 构建“50字摘要 + 3核心标签”的用户提示词
     */
    public static String buildSummaryTagPrompt(String content) {
        return "你是知识管理助手。请基于输入文本执行：" +
                "1) 输出约50字中文摘要；2) 提取3个核心标签。" +
                "仅返回 JSON，格式为：" +
                "{\"summary\":\"...\",\"tags\":[\"标签1\",\"标签2\",\"标签3\"]}。" +
                "不要输出 markdown，不要解释。文本如下：\n" + content;
    }
}

# 测评报告接口完善计划

## 一、当前已有接口

### 1. 获取测评报告 `GET /api/assessment/report/{assessmentId}`

**现有响应字段（AssessmentReport）：**

| 字段            | 类型            | 说明             | 状态    |
| --------------- | --------------- | ---------------- | ------- |
| assessmentId    | number          | 测评ID           | ✅ 已有 |
| assessmentType  | number          | 测评类型         | ✅ 已有 |
| finalScore      | number          | 最终得分         | ✅ 已有 |
| accuracy        | number          | 正确率           | ✅ 已有 |
| totalTime       | number          | 总用时(秒)       | ✅ 已有 |
| avgTime         | number          | 平均答题时间(秒) | ✅ 已有 |
| minTime         | number          | 最短答题时间(秒) | ✅ 已有 |
| maxTime         | number          | 最长答题时间(秒) | ✅ 已有 |
| totalQuestions  | number          | 总题数           | ✅ 已有 |
| correctCount    | number          | 答对数量         | ✅ 已有 |
| wrongCount      | number          | 答错数量         | ✅ 已有 |
| abilityScore    | number          | 能力值评分       | ✅ 已有 |
| confidenceLevel | number          | 置信度(0-1)      | ✅ 已有 |
| hasHistory      | boolean         | 是否有历史记录   | ✅ 已有 |
| lastScore       | number          | 上次得分         | ✅ 已有 |
| scoreChange     | number          | 分数变化         | ✅ 已有 |
| masteredKpoints | KpointMastery[] | 已掌握知识点     | ✅ 已有 |
| weakKpoints     | KpointMastery[] | 薄弱知识点       | ✅ 已有 |
| answerDetails   | AnswerDetail[]  | 答题详情         | ✅ 已有 |
| aiComment       | string          | AI评语           | ✅ 已有 |
| suggestions     | string[]        | 学习建议         | ✅ 已有 |
| completedAt     | string          | 完成时间         | ✅ 已有 |
| chapterName     | string          | 章节名称         | ✅ 已有 |
| kpointName      | string          | 知识点名称       | ✅ 已有 |

---

## 二、需要新增的接口字段（已完成 ✅）

### 1. 历史对比数据扩展 ✅

**已在 AssessmentReportVO 中新增：该单元上一次的测评数据**

| 字段                | 类型   | 说明         | 状态      |
| ------------------- | ------ | ------------ | --------- |
| lastAccuracy        | number | 上次正确率   | ✅ 已完成 |
| accuracyChange      | number | 正确率变化   | ✅ 已完成 |
| lastAbilityScore    | number | 上次能力值   | ✅ 已完成 |
| abilityChange       | number | 能力值变化   | ✅ 已完成 |
| lastConfidenceLevel | number | 上次信心指数 | ✅ 已完成 |
| confidenceChange    | number | 信心指数变化 | ✅ 已完成 |

### 2. 答题统计扩展 ✅

**已在 AssessmentReportVO 中新增：**

| 字段            | 类型   | 说明                       | 状态      |
| --------------- | ------ | -------------------------- | --------- |
| avgTimeStandard | number | 该单元所有用户平均评测时间 | ✅ 已完成 |

### 3. 排名数据 ✅

**已新增 RankingInfoVO 结构，并集成到 AssessmentReportVO 中：**

| 字段              | 类型   | 说明                                         | 状态      |
| ----------------- | ------ | -------------------------------------------- | --------- |
| classRank         | number | 同年级排名（同一年度例如从9月到第二年的6月） | ✅ 已完成 |
| classTotal        | number | 同年级总人数                                 | ✅ 已完成 |
| classBeatPercent  | number | 同年级超越百分比                             | ✅ 已完成 |
| systemRank        | number | 系统排名                                     | ✅ 已完成 |
| systemTotal       | number | 系统总人数                                   | ✅ 已完成 |
| systemBeatPercent | number | 超越系统百分比                               | ✅ 已完成 |

### 4. 本人历史趋势数据 ✅

**已新增接口：`GET /api/assessment/history-trend/{chapterId}`**

**请求参数：**

- chapterId: 章节ID (路径参数)
- limit: 返回记录数（默认6，查询参数）

**响应结构 HistoryTrendVO[]：**

| 字段         | 类型   | 说明       | 状态      |
| ------------ | ------ | ---------- | --------- |
| date         | string | 测评日期   | ✅ 已完成 |
| score        | number | 得分       | ✅ 已完成 |
| ability      | number | 能力值     | ✅ 已完成 |
| accuracy     | number | 正确率     | ✅ 已完成 |
| assessmentId | number | 测评ID     | ✅ 已完成 |

---

## 三、实现说明

### 完成日期：2025-12-26

### 修改的文件：

1. **VO层**
   - `AssessmentReportVO.java` - 新增历史对比字段、avgTimeStandard、排名信息
   - `RankingInfoVO.java` - 新增排名信息数据结构
   - `HistoryTrendVO.java` - 新增历史趋势数据结构

2. **Service层**
   - `AssessmentService.java` - 新增 `getHistoryTrend()` 方法
   - `AssessmentServiceImpl.java` - 实现以下逻辑：
     - 历史对比字段计算（lastAccuracy、accuracyChange等）
     - 平均时间标准计算 `calculateAvgTimeStandard()`
     - 排名信息计算 `calculateRankingInfo()`（包含系统排名和同学年排名）
     - 历史趋势查询 `getHistoryTrend()`

3. **Controller层**
   - `AssessmentController.java` - 新增 `GET /api/assessment/history-trend/{chapterId}` 接口

### 排名计算逻辑说明：

1. **系统排名**：按该单元所有用户的最高分进行排名
2. **同年级排名**：
   - 学年定义：9月1日 至 次年6月30日
   - 按同一学年内该单元的最高分进行排名
   - 超越百分比 = (总人数 - 排名) / (总人数 - 1)

### 编译验证：

```bash
mvn compile -am -pl ruoyi-ailearning -DskipTests
# BUILD SUCCESS
```

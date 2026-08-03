# Requirements Document

## Introduction

一个安卓转盘选择 App（Wheel Picker）。用户可自由编辑转盘选项（文字、颜色、权重），点击旋转后随机选出结果。适用于活动抽奖、课堂点名、团队决策等场景。

## Glossary

- **转盘（Wheel）**: 圆形界面，按扇区展示全部选项，旋转后指针指向结果
- **选项（Option）**: 转盘上的一个扇区条目，包含文字、颜色、权重
- **权重（Weight）**: 选项被随机选中的相对概率系数
- **指针（Pointer）**: 转盘顶部固定的箭头标记，指示最终命中的扇区

## Requirements

### Requirement 1: 转盘展示

**User Story:** AS 用户, I want 看到包含全部选项的圆形转盘, so that 清晰直观地执行抽奖选择。

#### Acceptance Criteria

1. WHEN 应用启动，系统 SHALL 渲染一个包含全部已配置选项的转盘
2. WHILE 转盘静止，系统 SHALL 在对应扇区内显示每个选项的文字
3. 系统 SHALL 使转盘扇区数量等于当前选项总数
4. IF 选项总数少于 2，系统 SHALL 在转盘区域显示空态提示而非可旋转转盘

### Requirement 2: 旋转与结果

**User Story:** AS 用户, I want 点击按钮让转盘旋转, so that 从选项集中随机选出一个结果。

#### Acceptance Criteria

1. WHEN 用户点击旋转按钮，系统 SHALL 播放转盘旋转动画
2. WHEN 旋转动画结束，系统 SHALL 展示指针命中的选项作为本次结果
3. WHILE 正常模式，系统 SHALL 按选项权重比例计算停止位置
4. WHILE 转盘旋转中，系统 SHALL 禁用旋转按钮防止重复触发
5. WHEN 旋转完成，系统 SHALL 清理旋转中的状态并恢复按钮可用

### Requirement 3: 选项编辑

**User Story:** AS 用户, I want 增删改转盘选项, so that 随时调整抽奖内容。

#### Acceptance Criteria

1. WHEN 用户添加新选项，系统 SHALL 将选项追加到转盘末尾
2. WHEN 用户修改选项文字，系统 SHALL 更新对应扇区的显示文字
3. WHEN 用户删除选项，系统 SHALL 从转盘中移除对应扇区
4. WHEN 用户提交空白选项文字，系统 SHALL 拒绝保存并给出提示
5. 系统 SHALL 限制选项总数不超过 20，达到上限时禁止继续添加
6. WHEN 用户清空全部选项，系统 SHALL 恢复为默认示例选项集

### Requirement 4: 颜色编辑

**User Story:** AS 用户, I want 为选项设置颜色, so that 转盘视觉效果更清晰美观。

#### Acceptance Criteria

1. WHEN 用户为选项选择颜色，系统 SHALL 用该颜色填充对应扇区
2. 系统 SHALL 为每个新增选项自动分配与相邻选项不同的颜色
3. WHEN 用户未自定义颜色，系统 SHALL 使用自动分配的默认颜色

### Requirement 5: 权重控制

**User Story:** AS 用户, I want 为选项设置权重, so that 控制每个选项被选中的概率。

#### Acceptance Criteria

1. WHEN 用户为选项设置权重，系统 SHALL 按权重比例决定随机选中概率
2. WHEN 全部选项权重一致，系统 SHALL 使每个选项选中概率相等
3. 系统 SHALL 限制权重范围为 1 至 100 的整数
4. WHEN 用户输入超出范围的权重值，系统 SHALL 自动钳制到合法范围

### Requirement 6: 数据持久化

**User Story:** AS 用户, I want 转盘配置在重启后保留, so that 无需每次重新编辑。

#### Acceptance Criteria

1. WHEN 选项配置发生变更，系统 SHALL 将配置写入本地存储
2. WHEN 应用重启，系统 SHALL 恢复最近一次保存的转盘配置
3. WHILE 本地无历史配置，系统 SHALL 使用内置默认选项集

### Requirement 7: 历史记录

**User Story:** AS 用户, I want 查看历史旋转结果, so that 事后核对抽奖过程与公平性。

#### Acceptance Criteria

1. WHEN 一次旋转完成，系统 SHALL 记录结果选项与时间戳
2. WHEN 用户打开历史记录页面，系统 SHALL 按时间倒序展示记录
3. WHEN 用户清空历史，系统 SHALL 移除全部记录

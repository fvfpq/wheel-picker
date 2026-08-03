# 需求实施计划

- [x] 1. 搭建 Android 工程骨架与构建配置
   - 创建 `settings.gradle.kts`、根 `build.gradle.kts`、`app/build.gradle.kts`、Gradle Wrapper
   - 配置 Kotlin + Compose + Material3 + kotlinx.serialization + DataStore 依赖
   - 创建 AndroidManifest、主题资源、图标占位资源
   - 创建包结构 `com.example.wheelpicker`（ui / domain / data / viewmodel）

- [x] 2. 实现数据模型与持久化层
  - [x] 2.1 定义数据模型
    - 编写 `WheelOption`、`WheelConfig`、`SpinRecord` 数据类（kotlinx.serialization）
    - 实现默认示例选项集与自动配色逻辑
    - 权重钳制逻辑（1..100）

  - [x] 2.2 实现 `OptionRepository`
    - 基于 Preferences DataStore 读写 `WheelConfig`（JSON 序列化）
    - 提供 `config: Flow<WheelConfig>`、`save/updateOptions/updatePassword` 接口
    - 历史记录独立存储（键 `spin_history`）与增删读接口

  - [x] 2.3 编写数据层单元测试
    - 测试默认配置、权重钳制、配置 JSON 序列化往返
    - 测试 Repository 读写与默认回退逻辑

- [x] 3. 实现领域层：转盘引擎与选择器
  - [x] 3.1 实现选择器
    - 编写 `OptionSelector` 接口、`RandomSelector`（按权重累积区间随机）、`ForcedSelector`

  - [x] 3.2 实现 `SpinEngine`
    - 权重归一化扇区角度计算，保证扇区和恒为 360°
    - `computeTargetRotation`：随机圈数(4..8) + 目标扇区中心对齐
    - 最终角度到扇区的唯一映射

  - [x] 3.3 实现 `BackdoorController`
    - 顶部连点 5 次检测、密码校验
    - 指定/清除/消费 `forcedOptionId`（幂等消费）

  - [x] 3.4 编写领域层单元测试
    - `RandomSelector` 大规模抽样统计概率近似权重占比（容差 2%）
    - `SpinEngine` 角度映射唯一性、扇区和 = 360°、指定结果收敛到目标扇区
    - `BackdoorController` 连点计数、密码、消费幂等性

  - [x] 3.5 检查点 - 确保所有测试通过,如有疑问请询问用户

- [x] 4. 实现转盘绘制与旋转动画（Compose UI）
  - [x] 4.1 实现 `WheelCanvas`
    - Canvas 按权重占比绘制扇区（drawArc + 填充色）
    - 扇区文字沿中分角绘制并自适应字号
    - 顶部指针与中心圆点绘制

  - [x] 4.2 实现转盘页面与旋转状态机
    - `WheelViewModel` 维护 Idle/Spinning 状态与配置流
    - 旋转按钮触发动画，旋转中禁用
    - `Animatable<Float>` 驱动旋转角度，结束后展示结果并写入历史

- [x] 5. 实现选项编辑功能
  - [x] 5.1 实现编辑页面
    - 选项列表增删改（文字）
    - 空白文字校验与提示、上限 20 个选项限制
    - 清空后恢复默认选项集

  - [x] 5.2 实现颜色与权重编辑
    - 颜色选择器（预设色板），新增选项自动配色
    - 权重输入，失焦钳制 1..100
    - 保存后配置持久化并同步转盘

- [x] 6. 实现隐藏后台控制
  - [x] 6.1 实现入口与密码校验
    - 顶部连续点击 5 次弹出密码对话框
    - 密码校验通过进入后台模式，错误提示

  - [x] 6.2 实现后台控制界面
    - 后台列表展示全部选项，选择下一次结果
    - 清除指定、展示当前指定状态（隐蔽标记）
    - 旋转完成后自动消费指定状态

- [x] 7. 实现历史记录
  - [x] 7.1 实现历史记录页面与持久化
    - 按时间倒序展示每次旋转结果
    - 清空历史功能

- [x] 8. 检查点 - 完整构建验证与收尾
   - 领域层与数据模型单元测试已编写，并通过本地 JVM 等价断言验证（权重分布、角度映射、后台控制）
   - 工程结构完整：Gradle Wrapper、AGP/Kotlin/Compose 配置、Manifest、资源齐备，可由 Android Studio 直接打开构建
   - 完整 Gradle 构建因当前环境无法访问 Google 依赖仓库（maven.google.com 受限）未能在本地执行，需在具备 Android SDK 的环境构建

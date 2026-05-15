-- 根据 orderId 幂等校验
if redis.call('hexists', KEYS[2], ARGV[2]) == 1 then
    return redis.error_reply('OPERATION_ALREADY_EXECUTED')
end

local current = redis.call('get', KEYS[1])
-- 当前商品库存不存在
if current == false then
    return redis.error_reply('KEY_NOT_FOUND')
end
-- 当前商品库存不为数字
if tonumber(current) == nil then
    return redis.error_reply('current value is not a number')
end
-- 当前库存为 0
if tonumber(current) == 0 then
    return redis.error_reply('INVENTORY_IS_ZERO')
end
-- 当前库存不足扣减
if tonumber(current) < tonumber(ARGV[1]) then
    return redis.error_reply('INVENTORY_NOT_ENOUGH')
end

-- 扣减库存
local new = tonumber(current) - tonumber(ARGV[1])
redis.call('set', KEYS[1], tostring(new))

-- 获取 Redis 服务器的当前时间（秒和微秒）
local time = redis.call("time")
-- 转换为毫秒级时间戳
local currentTimeMillis = (time[1] * 1000) + math.floor(time[2] / 1000)

-- 使用哈希结构存储日志
redis.call('hset', KEYS[2], ARGV[2], cjson.encode({
    action = "decrease",
    from = current,
    to = new,
    change = ARGV[1],
    by = ARGV[2],
    timestamp = currentTimeMillis
}))

return new
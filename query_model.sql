use oso_monitor;

SELECT
  machine_rdb.id id,
  machine_rdb.jdbcUrl jdbcUrl,
  machine_rdb.username username,
  machine_rdb.password password,
  monitor_rdb.category category,
  monitor_rdb_content.sql sql_text,
  if(json_length(monitor_rdb_content.args) = 0, monitor_rdb.rags, monitor_rdb_content.args) args
FROM
  monitor_rdb_content,
  monitor_rdb_detail,
  monitor_rdb,
  machine_rdb,
  monitor,
  machine
WHERE monitor_rdb_detail.mon_rdb_id = monitor_rdb.id
AND monitor_rdb_detail.mac_rdb_id = machine_rdb.id
AND monitor.id = monitor_rdb.monitor_id
AND machine_rdb.machine_id = machine.id
AND monitor_rdb_content.id = monitor_rdb.content_id
AND monitor_rdb.category = machine_rdb.category
AND (monitor_rdb.category_version = machine_rdb.category_version or monitor_rdb.category_version = 'ALL');

show variables like "%tx%"; 
set tx_isolation = "READ-COMMITTED";

select args from monitor_rdb_content;

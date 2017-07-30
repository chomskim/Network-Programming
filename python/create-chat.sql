CREATE DATABASE chat;

create table chat_server_table (
msg_id		serial		primary key,
msg_date	date,
host_from	varchar(20),
port_from	int,
host_to		varchar(20),
port_to		int,
msg_data	varchar(40000)
);

create or replace function msg_send_by_trigger()
returns trigger
as $$
plpy.notice('TD["new"]={}'.format(TD["new"]))
host = TD["new"]["host_to"]
port = TD["new"]["port_to"]
msg = TD["new"]["msg_data"]
import socket
sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
ret = sock.sendto(msg.encode('utf-8'), (host, port))
plpy.notice('send len={} msg={}'.format(ret, msg))
$$ LANGUAGE plpythonu;

create trigger msg_send_trigger
after insert on chat_server_table
for each row
execute procedure msg_send_by_trigger();

INSERT INTO chat_server_table 
(msg_date, host_from, port_from, host_to, port_to, msg_data)
VALUES (now(), '192.168.219.154', 44099, '192.168.219.172', 56870, 'hello world!');

select * from chat_server_table;

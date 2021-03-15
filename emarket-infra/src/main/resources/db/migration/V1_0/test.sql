select receivedms0_.id as id1_3_,
        receivedms0_.block_time as block_ti2_3_,
         receivedms0_.json_serialized_msg as json_ser3_3_,
          receivedms0_.msg_creation_time as msg_crea4_3_,
           receivedms0_.msg_type as msg_type5_3_,
            receivedms0_.origin_message as origin_m6_3_,
             receivedms0_.received_time as received7_3_,
              receivedms0_.status as status8_3_,
               receivedms0_.topic as topic9_3_
 from public.received_msg_container receivedms0_
 where receivedms0_.block_time is null
 or receivedms0_.block_time<?
 order by receivedms0_.msg_creation_time desc limit ?
10-03-2021 10:57:31.931 [pool-3-thread-1] TRACE o.h.type.descriptor.sql.BasicBinder.bind - binding parameter [1] as [TIMESTAMP] - [2021-03-10T10:57:26.917]
10-03-2021 10:57:31.978 [pool-3-thread-1] TRACE o.h.t.descriptor.sql.BasicExtractor.extract - extracted value ([id1_3_] : [VARCHAR]) - [d3e3b46a-0a79-493d-9c4f-cc3c3ab88641]
10-03-2021 10:57:31.979 [pool-3-thread-1] TRACE o.h.t.descriptor.sql.BasicExtractor.extract - extracted value ([block_ti2_3_] : [TIMESTAMP]) - [null]
10-03-2021 10:57:31.980 [pool-3-thread-1] TRACE o.h.t.descriptor.sql.BasicExtractor.extract - extracted value ([json_ser3_3_] : [VARCHAR]) - [{"eventVersion":"1.0","eventOccuredOn":"2021-03-10T10:57:20.769","idUtilisateur":"999739","idObjet":null,"numVersionObjet":0,"typeObjet":"com.demo.web.emarket.domain.order.event.customer.model.CustomerModel","payload":{"id":null,"firstName":"madallin","lastName":"patrinoiu","numVersionObjet":0}}]
10-03-2021 10:57:31.981 [pool-3-thread-1] TRACE o.h.t.descriptor.sql.BasicExtractor.extract - extracted value ([msg_crea4_3_] : [TIMESTAMP]) - [2021-03-10T10:57:20.927]
10-03-2021 10:57:31.981 [pool-3-thread-1] TRACE o.h.t.descriptor.sql.BasicExtractor.extract - extracted value ([msg_type5_3_] : [VARCHAR]) - [com.demo.web.emarket.domain.order.event.customer.CustomerAdded]
10-03-2021 10:57:31.981 [pool-3-thread-1] TRACE o.h.t.descriptor.sql.BasicExtractor.extract - extracted value ([origin_m6_3_] : [VARCHAR]) - [app-demo]
10-03-2021 10:57:31.982 [pool-3-thread-1] TRACE o.h.t.descriptor.sql.BasicExtractor.extract - extracted value ([received7_3_] : [TIMESTAMP]) - [2021-03-10T10:57:31.823]
10-03-2021 10:5
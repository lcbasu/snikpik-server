# dukaankhata-server
dukaankhata-server

Choose Language -> Login -> Select User Category (Default to Employer) -> Create/Select Store -> Select App -> Employer Screens for Selected App.

DK Shop Screens

Shopkeeper:
Create Shop -> Create Username -> Add Products -> Add Categories
    -> Start Selling by sharing link
    -> Add Bank account details if you need to accept online payment
    -> Ship through ShipRocket
    -> Add custom domain
    -> Download APK and share your store app
    -> Send alerts to shopkeepers about the customer who should be contacted for repeat orders
    -> For 5K per month, we will do offline advertisement. We will hire a superman/women whose only work is to visit nearby houses and educate them about the advertiser.

Customers:
Visit Link -> Add to cart -> Login -> Add Address -> Select Payment Mode -> Place Order


## Cassandra Counters
https://docs.datastax.com/en/cql-oss/3.3/cql/cql_using/useCountersConcept.html

Because counters are implemented differently from other columns, counter columns can only be created in dedicated tables. A counter column must have the datatype counter data type. This data type cannot be assigned to a column that serves as the primary key or partition key. To implement a counter column, create a table that only includes:

Video Upload and Delivery

1. User opens app
2. User uploads the image/video
3. User submits the post (Only allow this if the user has uploaded the video to S3)
4. We save the only 2 feeds -> post and posts_by_user
5. Once the media processing is done, we reprocess the above 2 feed along with all the dependent feeds.


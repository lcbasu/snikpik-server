# dukaankhata-server

[![Release Master Build and Deploy to AWS Elastic Beanstalk](https://github.com/lcbasu/unbox-server/actions/workflows/master-build-deploy.yml/badge.svg)](https://github.com/lcbasu/unbox-server/actions/workflows/master-build-deploy.yml)


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



## Reading Instagram Media

#### Get list of all media for a user

```
https://graph.instagram.com/me/media?fields=id,caption,media_type,media_url,permalink,thumbnail_url,timestamp,username&access_token=IGQVJVU0FGZAHVjbG42eUZAWZAXkyd2VqOVZAxR0JuMlNQLWhibHpiMHA3cXVnU3ptSlRPOTNCWUctTEJUUm1wSjd0ZAGp3LTRDNUY1eV9BSldjNjNLa2xiZAnJ0T2piRzdod0JRTWZAkNGxqb2d3SEVOY1ZAnZAwZDZD
```

![readme_instagram_api.png](readme_instagram_api.png)

#### Media Types
1. Image (IMAGE) -> Directly available without any extra processing
2. Video (VIDEO) -> Directly available without any extra processing
3. Carousel (CAROUSEL_ALBUM) -> Needs extra processing


#### Get Media Details for CAROUSEL_ALBUM

1. Get the id of the Post
2. Get all the media children IDs using this API.
   1. https://graph.instagram.com/17994656425421820/children?access_token=IGQVJVU0FGZAHVjbG42eUZAWZAXkyd2VqOVZAxR0JuMlNQLWhibHpiMHA3cXVnU3ptSlRPOTNCWUctTEJUUm1wSjd0ZAGp3LTRDNUY1eV9BSldjNjNLa2xiZAnJ0T2piRzdod0JRTWZAkNGxqb2d3SEVOY1ZAnZAwZDZD
3. Run a loop through the media children IDs and get the media details for each child.
   1. https://graph.instagram.com/18046861537311623?fields=id,media_type,media_url,username,timestamp&access_token=IGQVJVU0FGZAHVjbG42eUZAWZAXkyd2VqOVZAxR0JuMlNQLWhibHpiMHA3cXVnU3ptSlRPOTNCWUctTEJUUm1wSjd0ZAGp3LTRDNUY1eV9BSldjNjNLa2xiZAnJ0T2piRzdod0JRTWZAkNGxqb2d3SEVOY1ZAnZAwZDZD

#### Fields for Media

`caption`
The Media's caption text. Not returnable for Media in albums.

`id`
The Media's ID.

`media_type`
The Media's type. Can be IMAGE, VIDEO, or CAROUSEL_ALBUM.

`media_url`
The Media's URL.

`permalink`
The Media's permanent URL. Will be omitted if the Media contains copyrighted material, or has been flagged for a copyright violation.

`thumbnail_url`
The Media's thumbnail image URL. Only available on VIDEO Media.

`timestamp`
The Media's publish date in ISO 8601 format.

`username`
The Media owner's username.


# Data cache

In-memory and persistent cache for Android

## Gradle
    
    repositories {
        maven{url "https://github.com/shaubert/maven-repo/raw/master/releases"}
    }
    dependencies {
        compile 'com.shaubert.cache:library:1.2.1'
    }

## Requirements

Android API >= 11

## Basic How-to

Create `Cache` instance and configure it like this:

    public static final int STORAGE_VERSION = 1;
    
    public Cache setupCache(Context context) {
        //EntryKeyFactory converts Class and optional data qualifier to cache entry key 
        EntryKeyFactory keyFactory = new DefaultEntryKeyFactory();
        
        //you can setup persistable storage for your data classes annotated with @PersistableData
        DataStorage dataStorage = FileStorage.newBuilder(context)
                .version(STORAGE_VERSION)
                .debugMode(true) //if you want to read logs and get exceptions if serialization failed
                .dataSerializer() //your serializer, by default it is JavaSerializer
                .defaultDataCallback() //if you want to override data loading
                .build();
        
        //EntryFactory creates cache entries, please check Entry class.
        EntryFactory entryFactory = new DefaultEntryFactory(keyFactory, dataStorage);
        
        //finally create cache
        return new DefaultCache(keyFactory, entryFactory);
    }

For example you have `Response` class:

    public static class Response {
        String someData;
    }

And you want to put `Response` into cache. You have to call:

    Response response = ...;
    Cache cache = ...;
    Entry<Response> entry = cache.get(Response.class);
    entry.setValue(response);
    
If you want to read your `Response` from cache:

    Cache cache = ...;
    Entry<Response> entry = cache.get(Response.class);
    Response response = entry.getValue();

### Persistance

If you want to make your response persistable add `PersistableData` annotation to `Response` class and pass not null `DataStorage` to `DefaultEntryFactory` constructor:

    @PersistableData(Response.PersistableCallback.class)
    public static class Response {
        String someData;

        public static class PersistableCallback implements PersistentEntryCallback<Response> {
            @Override
            public void onDataLoaded(Response response) {

            }

            @Override
            public void onDataLoadingError() {

            }

            @Override
            public void onEmptyDataLoaded() {

            }

            @Override
            public void onDataSaved(Response response) {

            }

            @Override
            public void onDataSavingError() {

            }
        }
    }

Note that by default `FileStorage` uses `JavaSerializer`. You can easily implement for example `GSONSerializer` just look at `DataSerializer` interface:
    
    public class GsonSerializer implements DataSerializer {
        private Gson gson;
    
        public GsonSerializer(Gson gson) {
            this.gson = gson;
        }
    
        @Override
        public boolean isApplicable(Class<?> aClass) {
            return true;
        }
    
        @Override
        public <T> void serialize(T t, OutputStream outputStream) throws IOException {
            JsonWriter jsonWriter = new JsonWriter(new OutputStreamWriter(outputStream));
            gson.toJson(t, t.getClass(), jsonWriter);
            jsonWriter.flush();
        }
    
        @Override
        public <T> T deserialize(Class<T> tClass, InputStream inputStream) throws IOException {
            JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream));
            return gson.fromJson(jsonReader, tClass);
        }
    }

Note that on persistable data `entry.getValue()` will return null while data is loading. If you want you can pass callback to `getValue` like this:

    ((AsyncEntry<Response>) t).getValue(new DataCallback<Response>() {
        @Override
        public void onDataResult(Response response) {
            //response or null if error occurred or response wasn't in storage
        }
    });

`DefaultEntryFactory` will create `AsyncEntry` for all classes with `PersistableData` annotation if `DataStorage` not null.

### Marks

Also you can manage marks to your entry. For example when you want to mark your cache entry as loading while executing request. Marks available out of the box:
*  `Entry.DIRTY_MARK`
*  `Entry.FAILURE_MARK`
*  `Entry.UPDATING_MARK`
  
### Mergeable Data

Implement `MergeableData` interface in your data class to automatically perform merge on`entry.setValue()` if cache entry contains data.
